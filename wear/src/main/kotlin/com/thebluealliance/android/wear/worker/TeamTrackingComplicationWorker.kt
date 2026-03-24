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
import com.thebluealliance.android.wear.tracker.TeamTrackerPreferences
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
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

    /** Intermediate result of fetching a team's event/match data. */
    private data class TeamEventData(
        val events: List<EventDto>,
        val currentEvent: EventDto?,
        val teamMatches: List<MatchDto>,
        val avatarBase64: String?,
    )

    override suspend fun doWork(): Result {
        return try {
            val trackerPrefs = TeamTrackerPreferences(applicationContext)
            val trackerTeam = trackerPrefs.teamNumber
            var anyActiveEvent = false

            if (trackerTeam.isNotBlank()) {
                val teamKey = "frc$trackerTeam"
                val data = fetchTeamEventData(teamKey)

                // Update all active complications
                val complicationIds = TeamTrackingComplicationPreferences.getActiveComplicationIds(applicationContext)
                for (complicationId in complicationIds) {
                    try {
                        val prefs = TeamTrackingComplicationPreferences(applicationContext, complicationId)
                        val hasActive = updateComplication(prefs, trackerTeam, data)
                        if (hasActive) anyActiveEvent = true
                    } catch (e: Exception) {
                        Log.e(TAG, "Failed to update complication $complicationId", e)
                    }
                }

                // Update app-level team tracker
                try {
                    val hasActive = updateAppTracker(trackerPrefs, trackerTeam, data)
                    if (hasActive) anyActiveEvent = true
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to update app tracker", e)
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

    // region Data fetching

    /** Fetch events, current event matches, and avatar for a team. */
    private suspend fun fetchTeamEventData(teamKey: String): TeamEventData {
        val year = LocalDate.now().year

        val events = try {
            api.getTeamEvents(teamKey, year)
        } catch (e: Exception) {
            Log.w(TAG, "Failed to fetch events for $teamKey", e)
            emptyList()
        }

        val currentEvent = findCurrentEvent(events, teamKey)

        val teamMatches = if (currentEvent != null) {
            try {
                api.getEventMatches(currentEvent.key)
                    .filter { match ->
                        val red = match.alliances?.red?.teamKeys ?: emptyList()
                        val blue = match.alliances?.blue?.teamKeys ?: emptyList()
                        teamKey in red || teamKey in blue
                    }
                    .sortedWith(
                        compareBy(
                            { compLevelOrder(it.compLevel) },
                            { it.setNumber },
                            { it.matchNumber },
                        ),
                    )
            } catch (e: Exception) {
                Log.w(TAG, "Failed to fetch matches for ${currentEvent.key}", e)
                emptyList()
            }
        } else {
            emptyList()
        }

        val avatarBase64 = fetchAvatar(teamKey, year)

        return TeamEventData(events, currentEvent, teamMatches, avatarBase64)
    }

    private suspend fun fetchAvatar(teamKey: String, year: Int): String? {
        return try {
            val media = api.getTeamMedia(teamKey, year)
            val avatar = media.firstOrNull { it.type == "avatar" }
                ?: api.getTeamMedia(teamKey, year - 1).firstOrNull { it.type == "avatar" }
            avatar?.base64Image
        } catch (e: Exception) {
            Log.w(TAG, "Failed to fetch avatar for $teamKey", e)
            null
        }
    }

    // endregion

    // region Complication update

    /** Returns true if the team has an active event. */
    private fun updateComplication(prefs: TeamTrackingComplicationPreferences, teamNumber: String, data: TeamEventData): Boolean {

        if (data.currentEvent == null) {
            prefs.matchLabel = ""
            prefs.matchTime = ""
            prefs.activeEventName = ""

            val nextEvent = findUpcomingEvent(data.events)
            if (nextEvent != null) {
                prefs.upcomingEventName = nextEvent.shortName ?: nextEvent.name
                prefs.upcomingEventDate = nextEvent.startDate?.let {
                    LocalDate.parse(it).format(DateTimeFormatter.ofPattern("MMM d"))
                } ?: ""
            } else {
                prefs.upcomingEventName = ""
                prefs.upcomingEventDate = ""
            }

            prefs.avatarBase64 = data.avatarBase64
            return false
        }

        val nextMatch = data.teamMatches.firstOrNull { (it.alliances?.red?.score ?: -1) < 0 }
        if (nextMatch != null) {
            prefs.matchLabel = getShortLabel(nextMatch)
            prefs.matchTime = formatMatchTime(nextMatch)
        } else {
            prefs.matchLabel = ""
            prefs.matchTime = ""
        }
        prefs.activeEventName = data.currentEvent.shortName ?: data.currentEvent.name

        prefs.avatarBase64 = data.avatarBase64
        return true
    }

    // endregion

    // region App tracker update

    /** Returns true if the team has an active event. */
    private suspend fun updateAppTracker(prefs: TeamTrackerPreferences, teamNumber: String, data: TeamEventData): Boolean {
        val teamKey = "frc$teamNumber"

        // Fetch team nickname
        prefs.teamNickname = try {
            api.getTeam(teamKey).nickname ?: ""
        } catch (e: Exception) {
            Log.w(TAG, "Failed to fetch team info for $teamKey", e)
            ""
        }

        prefs.avatarBase64 = data.avatarBase64

        if (data.currentEvent == null) {
            prefs.hasActiveEvent = false
            prefs.eventName = ""
            prefs.record = ""
            clearLastMatch(prefs)
            clearNextMatch(prefs)

            val upcoming = findAllUpcomingEvents(data.events)
            prefs.upcomingEvents = if (upcoming.isNotEmpty()) {
                upcoming.joinToString("|") { event ->
                    val name = event.shortName ?: event.name
                    val date = event.startDate?.let {
                        LocalDate.parse(it).format(DateTimeFormatter.ofPattern("MMM d"))
                    } ?: ""
                    "$date \u2014 $name"
                }
            } else {
                ""
            }
            return false
        }

        prefs.hasActiveEvent = true
        prefs.eventName = data.currentEvent.shortName ?: data.currentEvent.name
        prefs.upcomingEvents = ""

        // Compute qual record
        val qualMatches = data.teamMatches.filter {
            it.compLevel == "qm" && (it.alliances?.red?.score ?: -1) >= 0
        }
        var wins = 0
        var losses = 0
        var ties = 0
        for (match in qualMatches) {
            val alliance = if (teamKey in (match.alliances?.red?.teamKeys ?: emptyList())) "red" else "blue"
            when {
                match.winningAlliance == alliance -> wins++
                match.winningAlliance.isNullOrBlank() -> ties++
                else -> losses++
            }
        }
        prefs.record = "$wins-$losses-$ties"

        // Last played match
        val lastMatch = data.teamMatches.lastOrNull { (it.alliances?.red?.score ?: -1) >= 0 }
        if (lastMatch != null) {
            prefs.lastMatchLabel = getShortLabel(lastMatch)
            prefs.lastMatchRedTeams = teamKeysToNumbers(lastMatch.alliances?.red?.teamKeys)
            prefs.lastMatchBlueTeams = teamKeysToNumbers(lastMatch.alliances?.blue?.teamKeys)
            prefs.lastMatchRedScore = lastMatch.alliances?.red?.score ?: -1
            prefs.lastMatchBlueScore = lastMatch.alliances?.blue?.score ?: -1
            prefs.lastMatchWinningAlliance = lastMatch.winningAlliance ?: ""
            val lastAlliance = if (teamKey in (lastMatch.alliances?.red?.teamKeys ?: emptyList())) "red" else "blue"
            prefs.lastAlliance = lastAlliance
            prefs.lastMatchBonusRp = extractBonusRp(lastMatch, lastAlliance)
        } else {
            clearLastMatch(prefs)
        }

        // Next unplayed match
        val nextMatch = data.teamMatches.firstOrNull { (it.alliances?.red?.score ?: -1) < 0 }
        if (nextMatch != null) {
            prefs.nextMatchLabel = getShortLabel(nextMatch)
            prefs.nextMatchRedTeams = teamKeysToNumbers(nextMatch.alliances?.red?.teamKeys)
            prefs.nextMatchBlueTeams = teamKeysToNumbers(nextMatch.alliances?.blue?.teamKeys)
            prefs.nextMatchTimeIsEstimate = nextMatch.predictedTime != null
            prefs.nextMatchTime = formatMatchTimeRaw(nextMatch)
            prefs.nextAlliance = if (teamKey in (nextMatch.alliances?.red?.teamKeys ?: emptyList())) "red" else "blue"
        } else {
            clearNextMatch(prefs)
        }

        return true
    }

    private fun clearLastMatch(prefs: TeamTrackerPreferences) {
        prefs.lastMatchLabel = ""
        prefs.lastMatchRedTeams = ""
        prefs.lastMatchBlueTeams = ""
        prefs.lastMatchRedScore = -1
        prefs.lastMatchBlueScore = -1
        prefs.lastMatchWinningAlliance = ""
        prefs.lastAlliance = ""
        prefs.lastMatchBonusRp = 0
    }

    /** Extract bonus RPs (total minus win/tie RPs) from score_breakdown. */
    private fun extractBonusRp(match: MatchDto, alliance: String): Int {
        val breakdown = match.scoreBreakdown?.get(alliance)?.jsonObject ?: return 0
        val totalRp = breakdown["rp"]?.jsonPrimitive?.intOrNull ?: return 0
        val winRp = when {
            match.winningAlliance == alliance -> 2
            match.winningAlliance.isNullOrBlank() -> 1
            else -> 0
        }
        return (totalRp - winRp).coerceAtLeast(0)
    }

    private fun clearNextMatch(prefs: TeamTrackerPreferences) {
        prefs.nextMatchLabel = ""
        prefs.nextMatchRedTeams = ""
        prefs.nextMatchBlueTeams = ""
        prefs.nextMatchTime = ""
        prefs.nextMatchTimeIsEstimate = false
        prefs.nextAlliance = ""
    }

    // endregion

    // region Helpers

    private fun teamKeysToNumbers(keys: List<String>?): String =
        keys?.joinToString(", ") { it.removePrefix("frc") } ?: ""

    private fun findUpcomingEvent(events: List<EventDto>): EventDto? =
        findAllUpcomingEvents(events).firstOrNull()

    private fun findAllUpcomingEvents(events: List<EventDto>): List<EventDto> {
        val today = LocalDate.now()
        return events.filter { event ->
            val start = event.startDate?.let { LocalDate.parse(it) } ?: return@filter false
            !start.isBefore(today)
        }.sortedBy { it.startDate ?: "" }
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

    /** Format match time with estimate prefix for complication display. */
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

    /** Format match time without estimate prefix — app tracker stores the flag separately. */
    private fun formatMatchTimeRaw(match: MatchDto): String {
        val epochSeconds = match.predictedTime ?: match.time ?: return "TBD"
        val instant = Instant.ofEpochSecond(epochSeconds)
        val zoned = instant.atZone(ZoneId.systemDefault())
        val today = LocalDate.now()
        val matchDate = zoned.toLocalDate()

        return if (matchDate == today) {
            val amPm = if (zoned.hour < 12) "A" else "P"
            "${timeFormat.format(zoned)}$amPm"
        } else {
            zoned.format(DateTimeFormatter.ofPattern("EEE"))
        }
    }

    // endregion
}
