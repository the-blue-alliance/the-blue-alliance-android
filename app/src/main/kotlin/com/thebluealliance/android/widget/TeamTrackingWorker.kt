package com.thebluealliance.android.widget

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.Preferences
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.getAppWidgetState
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.thebluealliance.android.data.repository.EventRepository
import com.thebluealliance.android.data.repository.MatchRepository
import com.thebluealliance.android.data.repository.TeamRepository
import com.thebluealliance.android.domain.getShortLabel
import com.thebluealliance.android.domain.rpBonuses
import com.thebluealliance.android.domain.model.Match
import com.thebluealliance.android.domain.model.PlayoffType
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.firstOrNull
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.concurrent.TimeUnit

@HiltWorker
class TeamTrackingWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val matchRepository: MatchRepository,
    private val eventRepository: EventRepository,
    private val teamRepository: TeamRepository,
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        private const val TAG = "TeamTrackingWorker"
        private const val WORK_NAME = "team_tracking_widget_refresh"

        fun cancelPeriodicRefresh(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
        }

        fun enqueuePeriodicRefresh(context: Context) {
            val request = PeriodicWorkRequestBuilder<TeamTrackingWorker>(
                15, TimeUnit.MINUTES,
            ).build()
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                request,
            )
        }

        private val timeFormat = DateTimeFormatter.ofPattern("h:mm a", Locale.US)
        private val timeWithDayFormat = DateTimeFormatter.ofPattern("EEE h:mm a", Locale.US)
    }

    override suspend fun doWork(): Result {
        return try {
            val manager = GlanceAppWidgetManager(applicationContext)
            val glanceIds = manager.getGlanceIds(TeamTrackingWidget::class.java)

            for (glanceId in glanceIds) {
                try {
                    val state = getAppWidgetState(applicationContext, PreferencesGlanceStateDefinition, glanceId)
                    val teamKey = state[TeamTrackingWidgetKeys.TEAM_KEY] ?: continue
                    val teamNumber = teamKey.removePrefix("frc")

                    updateWidgetForTeam(teamKey, teamNumber, glanceId)
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to update widget $glanceId", e)
                }
            }

            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to update widgets", e)
            Result.retry()
        }
    }

    private suspend fun updateWidgetForTeam(
        teamKey: String,
        teamNumber: String,
        glanceId: androidx.glance.GlanceId,
    ) {
        // Fetch team info and avatar
        val year = LocalDate.now().year
        try { teamRepository.refreshTeam(teamKey) } catch (_: Exception) {}
        try { teamRepository.refreshTeamMedia(teamKey, year) } catch (_: Exception) {}
        val team = teamRepository.observeTeam(teamKey).firstOrNull()
        val teamNickname = team?.nickname ?: ""
        val avatar = teamRepository.observeTeamMedia(teamKey, year).firstOrNull()
            ?.firstOrNull { it.isAvatar }

        // Find the team's current event
        eventRepository.refreshTeamEvents(teamKey, year)
        val events = eventRepository.observeTeamEvents(teamKey, year).firstOrNull()
            ?: emptyList()
        val currentEvent = findCurrentEvent(events, teamKey)

        val now = Instant.now()
            .atZone(ZoneId.systemDefault())
            .format(timeFormat)

        if (currentEvent == null) {
            val today = LocalDate.now()
            val upcomingEvents = events.filter { event ->
                val start = event.startDate?.let { LocalDate.parse(it) } ?: return@filter false
                !start.isBefore(today)
            }.sortedBy { it.startDate }.take(3)

            val upcomingEventsData = upcomingEvents.joinToString("\n") { event ->
                val name = event.shortName ?: event.name
                val city = listOfNotNull(event.city, event.state).joinToString(", ")
                val date = event.startDate?.let {
                    LocalDate.parse(it).format(DateTimeFormatter.ofPattern("MMM d", Locale.US))
                } ?: ""
                "$name|$city|$date"
            }

            updateAppWidgetState(applicationContext, glanceId) { prefs ->
                prefs[TeamTrackingWidgetKeys.TEAM_NUMBER] = teamNumber
                prefs[TeamTrackingWidgetKeys.TEAM_KEY] = teamKey
                prefs[TeamTrackingWidgetKeys.TEAM_NICKNAME] = teamNickname
                if (avatar?.base64Image != null) {
                    prefs[TeamTrackingWidgetKeys.AVATAR_BASE64] = avatar.base64Image
                } else {
                    prefs.remove(TeamTrackingWidgetKeys.AVATAR_BASE64)
                }
                prefs.remove(TeamTrackingWidgetKeys.EVENT_KEY)
                prefs[TeamTrackingWidgetKeys.EVENT_NAME] = ""
                prefs[TeamTrackingWidgetKeys.RECORD] = ""
                prefs[TeamTrackingWidgetKeys.NEXT_ALLIANCE] = ""
                prefs[TeamTrackingWidgetKeys.LAST_UPDATED] = now
                if (upcomingEventsData.isNotEmpty()) {
                    prefs[TeamTrackingWidgetKeys.UPCOMING_EVENTS] = upcomingEventsData
                } else {
                    prefs.remove(TeamTrackingWidgetKeys.UPCOMING_EVENTS)
                }
                TeamTrackingWidgetKeys.allLastMatchKeys().forEach { prefs.remove(it) }
                TeamTrackingWidgetKeys.allNextMatchKeys().forEach { prefs.remove(it) }
            }
            TeamTrackingWidget().update(applicationContext, glanceId)
            Log.d(TAG, "Widget updated for $teamKey — no current event")
            return
        }

        val eventKey = currentEvent.key

        // Refresh match data from API
        matchRepository.refreshEventMatches(eventKey)

        val event = eventRepository.observeEvent(eventKey).firstOrNull()
        val eventName = event?.shortName ?: event?.name ?: eventKey
        val playoffType = event?.playoffType ?: PlayoffType.BRACKET_8_TEAM

        val matches = matchRepository.observeEventMatches(eventKey).firstOrNull()
            ?: emptyList()

        // Filter to matches involving this team
        val teamMatches = matches.filter { match ->
            teamKey in match.redTeamKeys || teamKey in match.blueTeamKeys
        }.sortedWith(
            compareBy({ it.compLevel.order }, { it.setNumber }, { it.matchNumber })
        )

        val record = computeRecord(teamMatches, teamKey)

        val playedMatches = teamMatches.filter { it.redScore >= 0 }
        val unplayedMatches = teamMatches.filter { it.redScore < 0 }
        val lastMatch = playedMatches.lastOrNull()
        val nextMatch = unplayedMatches.firstOrNull()

        updateAppWidgetState(applicationContext, glanceId) { prefs ->
            prefs[TeamTrackingWidgetKeys.TEAM_NUMBER] = teamNumber
            prefs[TeamTrackingWidgetKeys.TEAM_KEY] = teamKey
            prefs[TeamTrackingWidgetKeys.TEAM_NICKNAME] = teamNickname
            if (avatar?.base64Image != null) {
                prefs[TeamTrackingWidgetKeys.AVATAR_BASE64] = avatar.base64Image
            } else {
                prefs.remove(TeamTrackingWidgetKeys.AVATAR_BASE64)
            }
            prefs[TeamTrackingWidgetKeys.EVENT_KEY] = eventKey
            prefs[TeamTrackingWidgetKeys.EVENT_NAME] = eventName
            prefs[TeamTrackingWidgetKeys.RECORD] = record
            val nextAlliance = when {
                nextMatch != null && teamKey in nextMatch.redTeamKeys -> "red"
                nextMatch != null && teamKey in nextMatch.blueTeamKeys -> "blue"
                else -> ""
            }
            prefs[TeamTrackingWidgetKeys.NEXT_ALLIANCE] = nextAlliance
            prefs.remove(TeamTrackingWidgetKeys.UPCOMING_EVENTS)
            prefs[TeamTrackingWidgetKeys.LAST_UPDATED] = now

            if (lastMatch != null) {
                prefs[TeamTrackingWidgetKeys.LAST_MATCH_LABEL] = lastMatch.getShortLabel(playoffType)
                prefs[TeamTrackingWidgetKeys.LAST_MATCH_RED_TEAMS] = lastMatch.redTeamKeys.joinToString(",") { it.removePrefix("frc") }
                prefs[TeamTrackingWidgetKeys.LAST_MATCH_BLUE_TEAMS] = lastMatch.blueTeamKeys.joinToString(",") { it.removePrefix("frc") }
                prefs[TeamTrackingWidgetKeys.LAST_MATCH_RED_SCORE] = lastMatch.redScore.toString()
                prefs[TeamTrackingWidgetKeys.LAST_MATCH_BLUE_SCORE] = lastMatch.blueScore.toString()
                prefs[TeamTrackingWidgetKeys.LAST_MATCH_WINNING_ALLIANCE] = lastMatch.winningAlliance ?: ""
                val rp = lastMatch.rpBonuses()
                if (rp != null) {
                    prefs[TeamTrackingWidgetKeys.LAST_MATCH_RED_RP] = rp.red.joinToString(",")
                    prefs[TeamTrackingWidgetKeys.LAST_MATCH_BLUE_RP] = rp.blue.joinToString(",")
                } else {
                    prefs.remove(TeamTrackingWidgetKeys.LAST_MATCH_RED_RP)
                    prefs.remove(TeamTrackingWidgetKeys.LAST_MATCH_BLUE_RP)
                }
            } else {
                TeamTrackingWidgetKeys.allLastMatchKeys().forEach { prefs.remove(it) }
            }

            if (nextMatch != null) {
                prefs[TeamTrackingWidgetKeys.NEXT_MATCH_LABEL] = nextMatch.getShortLabel(playoffType)
                prefs[TeamTrackingWidgetKeys.NEXT_MATCH_RED_TEAMS] = nextMatch.redTeamKeys.joinToString(",") { it.removePrefix("frc") }
                prefs[TeamTrackingWidgetKeys.NEXT_MATCH_BLUE_TEAMS] = nextMatch.blueTeamKeys.joinToString(",") { it.removePrefix("frc") }
                prefs[TeamTrackingWidgetKeys.NEXT_MATCH_TIME] = formatMatchTime(nextMatch)
                prefs[TeamTrackingWidgetKeys.NEXT_MATCH_TIME_IS_ESTIMATE] = isTimeEstimate(nextMatch).toString()
            } else {
                TeamTrackingWidgetKeys.allNextMatchKeys().forEach { prefs.remove(it) }
            }
        }
        TeamTrackingWidget().update(applicationContext, glanceId)
        Log.d(TAG, "Widget updated for $teamKey at $eventKey")
    }

    /**
     * Pick the best event to display: currently running > most recently ended.
     * Among concurrent events (e.g. division + Einstein at champs), prefer the one
     * that still has unplayed matches for this team.
     */
    private suspend fun findCurrentEvent(
        events: List<com.thebluealliance.android.domain.model.Event>,
        teamKey: String,
    ): com.thebluealliance.android.domain.model.Event? {
        if (events.isEmpty()) return null
        val today = LocalDate.now()
        val currentEvents = events.filter { event ->
            val start = event.startDate?.let { LocalDate.parse(it) } ?: return@filter false
            val end = event.endDate?.let { LocalDate.parse(it) } ?: start
            today in start..end
        }
        if (currentEvents.size > 1) {
            // Multiple concurrent events (e.g. champs division + Einstein).
            // Prefer the one with unplayed matches for this team.
            for (event in currentEvents) {
                try { matchRepository.refreshEventMatches(event.key) } catch (_: Exception) {}
                val matches = matchRepository.observeEventMatches(event.key).firstOrNull()
                    ?: emptyList()
                val hasUnplayed = matches.any { match ->
                    (teamKey in match.redTeamKeys || teamKey in match.blueTeamKeys) &&
                        match.redScore < 0
                }
                if (hasUnplayed) return event
            }
            // All events fully played — return the last one (likely Einstein/finals)
            return currentEvents.last()
        }
        if (currentEvents.isNotEmpty()) return currentEvents.first()

        // Most recently ended event (within the last 3 days, scores may still be updating)
        val recentPast = events.filter { event ->
            val end = event.endDate?.let { LocalDate.parse(it) } ?: return@filter false
            end.isBefore(today) && end.isAfter(today.minusDays(4))
        }.maxByOrNull { it.endDate ?: "" }
        if (recentPast != null) return recentPast

        // Don't return upcoming events as "current" — they'll show in the upcoming events section
        return null
    }

    private fun computeRecord(matches: List<Match>, teamKey: String): String {
        var wins = 0
        var losses = 0
        var ties = 0
        for (match in matches) {
            if (match.redScore < 0) continue
            val isOnRed = teamKey in match.redTeamKeys
            when {
                match.winningAlliance == "red" && isOnRed -> wins++
                match.winningAlliance == "blue" && !isOnRed -> wins++
                match.winningAlliance == "" || match.winningAlliance == null -> ties++
                else -> losses++
            }
        }
        return "$wins-$losses-$ties"
    }

    private fun formatMatchTime(match: Match): String {
        val epochSeconds = match.predictedTime ?: match.time ?: return "TBD"
        val instant = Instant.ofEpochSecond(epochSeconds)
        val zoned = instant.atZone(ZoneId.systemDefault())
        val today = LocalDate.now()
        val format = if (zoned.toLocalDate() == today) timeFormat else timeWithDayFormat
        return format.format(zoned)
    }

    private fun isTimeEstimate(match: Match): Boolean {
        return match.predictedTime != null && match.time != null &&
            kotlin.math.abs(match.predictedTime - match.time) > 60
    }
}
