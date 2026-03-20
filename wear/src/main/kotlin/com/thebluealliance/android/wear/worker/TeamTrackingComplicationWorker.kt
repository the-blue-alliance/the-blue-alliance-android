package com.thebluealliance.android.wear.worker

import android.content.ComponentName
import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.wear.watchface.complications.datasource.ComplicationDataSourceUpdateRequester
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.thebluealliance.android.wear.complication.TeamTrackingComplicationPreferences
import com.thebluealliance.android.wear.complication.TeamTrackingComplicationService
import com.thebluealliance.android.wear.data.WearTbaApi
import com.thebluealliance.android.wear.data.dto.EventDto
import com.thebluealliance.android.wear.data.dto.MatchDto
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

@HiltWorker
class TeamTrackingComplicationWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val api: WearTbaApi,
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        private const val TAG = "ComplicationRefresh"
        private const val WORK_NAME = "complication_refresh"
        private const val FAST_WORK_NAME = "complication_fast_refresh"

        private val timeFormat = DateTimeFormatter.ofPattern("h:mm")

        fun enqueuePeriodicRefresh(context: Context) {
            val request = PeriodicWorkRequestBuilder<TeamTrackingComplicationWorker>(
                6, TimeUnit.HOURS,
            ).build()
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                request,
            )
        }

        fun cancelPeriodicRefresh(context: Context) {
            val wm = WorkManager.getInstance(context)
            wm.cancelUniqueWork(WORK_NAME)
            wm.cancelUniqueWork(FAST_WORK_NAME)
        }

        private fun enqueueFastRefresh(context: Context) {
            val request = OneTimeWorkRequestBuilder<TeamTrackingComplicationWorker>()
                .setInitialDelay(15, TimeUnit.MINUTES)
                .build()
            WorkManager.getInstance(context).enqueueUniqueWork(
                FAST_WORK_NAME,
                ExistingWorkPolicy.REPLACE,
                request,
            )
        }

        fun enqueueImmediateRefresh(context: Context) {
            val request = OneTimeWorkRequestBuilder<TeamTrackingComplicationWorker>().build()
            WorkManager.getInstance(context).enqueueUniqueWork(
                "complication_immediate_refresh",
                ExistingWorkPolicy.REPLACE,
                request,
            )
        }
    }

    override suspend fun doWork(): Result {
        return try {
            val complicationIds = TeamTrackingComplicationPreferences.getActiveComplicationIds(applicationContext)
            var anyActiveEvent = false

            for (complicationId in complicationIds) {
                try {
                    val prefs = TeamTrackingComplicationPreferences(applicationContext, complicationId)
                    val teamNumber = prefs.teamNumber
                    if (teamNumber.isBlank()) continue

                    val hasActive = updateComplication(prefs, teamNumber)
                    if (hasActive) anyActiveEvent = true
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to update complication $complicationId", e)
                }
            }

            if (anyActiveEvent) {
                enqueueFastRefresh(applicationContext)
            }

            // Request complication data update from the watch face
            requestComplicationUpdate()

            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to update complications", e)
            Result.retry()
        }
    }

    private fun requestComplicationUpdate() {
        val requester = ComplicationDataSourceUpdateRequester.create(
            applicationContext,
            ComponentName(applicationContext, TeamTrackingComplicationService::class.java),
        )
        requester.requestUpdateAll()
    }

    /** Returns true if the team has an active event. */
    private suspend fun updateComplication(prefs: TeamTrackingComplicationPreferences, teamNumber: String): Boolean {
        val teamKey = "frc$teamNumber"
        val year = LocalDate.now().year

        // Fetch team events
        val events = try {
            api.getTeamEvents(teamKey, year)
        } catch (e: Exception) {
            Log.w(TAG, "Failed to fetch events for $teamKey", e)
            emptyList()
        }

        val currentEvent = findCurrentEvent(events, teamKey)
        if (currentEvent == null) {
            prefs.matchLabel = ""
            prefs.matchTime = ""
            prefs.hasActiveEvent = false
            prefs.activeEventName = ""

            // Find next upcoming event
            val today = LocalDate.now()
            val nextEvent = events.filter { event ->
                val start = event.startDate?.let { LocalDate.parse(it) } ?: return@filter false
                !start.isBefore(today)
            }.minByOrNull { it.startDate ?: "" }

            if (nextEvent != null) {
                prefs.upcomingEventName = nextEvent.shortName ?: nextEvent.name
                prefs.upcomingEventDate = nextEvent.startDate?.let {
                    LocalDate.parse(it).format(DateTimeFormatter.ofPattern("MMM d"))
                } ?: ""
            } else {
                prefs.upcomingEventName = ""
                prefs.upcomingEventDate = ""
            }

            fetchAndStoreAvatar(prefs, teamKey, year)
            return false
        }

        // Fetch matches for this event
        val matches = try {
            api.getEventMatches(currentEvent.key)
        } catch (e: Exception) {
            Log.w(TAG, "Failed to fetch matches for ${currentEvent.key}", e)
            emptyList()
        }

        // Filter to this team's matches, sorted
        val teamMatches = matches.filter { match ->
            val red = match.alliances?.red?.teamKeys ?: emptyList()
            val blue = match.alliances?.blue?.teamKeys ?: emptyList()
            teamKey in red || teamKey in blue
        }.sortedWith(
            compareBy({ compLevelOrder(it.compLevel) }, { it.setNumber }, { it.matchNumber })
        )

        val unplayedMatches = teamMatches.filter { (it.alliances?.red?.score ?: -1) < 0 }
        val nextMatch = unplayedMatches.firstOrNull()

        if (nextMatch != null) {
            prefs.matchLabel = getShortLabel(nextMatch)
            prefs.matchTime = formatMatchTime(nextMatch)
        } else {
            prefs.matchLabel = ""
            prefs.matchTime = ""
        }
        prefs.hasActiveEvent = true
        prefs.activeEventName = currentEvent.shortName ?: currentEvent.name

        fetchAndStoreAvatar(prefs, teamKey, year)

        return true
    }

    private suspend fun fetchAndStoreAvatar(prefs: TeamTrackingComplicationPreferences, teamKey: String, year: Int) {
        try {
            val media = api.getTeamMedia(teamKey, year)
            val avatar = media.firstOrNull { it.type == "avatar" }
                ?: api.getTeamMedia(teamKey, year - 1).firstOrNull { it.type == "avatar" }
            prefs.avatarBase64 = avatar?.base64Image
        } catch (e: Exception) {
            Log.w(TAG, "Failed to fetch avatar for $teamKey", e)
        }
    }

    /**
     * Find the current event: running today > recently ended (within 3 days).
     * Among concurrent events, prefer the one with unplayed matches.
     */
    private suspend fun findCurrentEvent(events: List<EventDto>, teamKey: String): EventDto? {
        if (events.isEmpty()) return null
        val today = LocalDate.now()

        val currentEvents = events.filter { event ->
            val start = event.startDate?.let { LocalDate.parse(it) } ?: return@filter false
            val end = event.endDate?.let { LocalDate.parse(it) } ?: start
            today in start..end
        }

        if (currentEvents.size > 1) {
            for (event in currentEvents) {
                try {
                    val matches = api.getEventMatches(event.key)
                    val hasUnplayed = matches.any { match ->
                        val red = match.alliances?.red?.teamKeys ?: emptyList()
                        val blue = match.alliances?.blue?.teamKeys ?: emptyList()
                        (teamKey in red || teamKey in blue) && (match.alliances?.red?.score ?: -1) < 0
                    }
                    if (hasUnplayed) return event
                } catch (e: Exception) {
                    Log.w(TAG, "Failed to check matches for ${event.key}", e)
                }
            }
            return currentEvents.maxByOrNull { it.eventType ?: 0 }
        }

        if (currentEvents.isNotEmpty()) return currentEvents.first()

        // Most recently ended (within 3 days)
        val recentPast = events.filter { event ->
            val end = event.endDate?.let { LocalDate.parse(it) } ?: return@filter false
            end.isBefore(today) && end.isAfter(today.minusDays(4))
        }.maxByOrNull { it.endDate ?: "" }

        return recentPast
    }

    private fun compLevelOrder(compLevel: String): Int = when (compLevel) {
        "qm" -> 0
        "ef" -> 1
        "qf" -> 2
        "sf" -> 3
        "f" -> 4
        else -> Int.MAX_VALUE
    }

    /** Simplified short label — "Q18", "SF2-1", "F1-1" */
    private fun getShortLabel(match: MatchDto): String = when (match.compLevel) {
        "qm" -> "Q${match.matchNumber}"
        "ef" -> "EF${match.setNumber}-${match.matchNumber}"
        "qf" -> "QF${match.setNumber}-${match.matchNumber}"
        "sf" -> "SF${match.setNumber}-${match.matchNumber}"
        "f" -> "F${match.setNumber}-${match.matchNumber}"
        else -> "${match.compLevel}${match.setNumber}-${match.matchNumber}"
    }

    private fun formatMatchTime(match: MatchDto): String {
        val epochSeconds = match.predictedTime ?: match.time ?: return "TBD"
        val instant = Instant.ofEpochSecond(epochSeconds)
        val zoned = instant.atZone(ZoneId.systemDefault())
        val today = LocalDate.now()
        val matchDate = zoned.toLocalDate()
        val prefix = if (match.predictedTime != null) "~" else ""

        return if (matchDate == today) {
            val amPm = if (zoned.hour < 12) "A" else "P"
            "$prefix${timeFormat.format(zoned)}$amPm"
        } else {
            zoned.format(DateTimeFormatter.ofPattern("EEE"))
        }
    }
}
