package com.thebluealliance.android.tracking

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import androidx.core.app.NotificationCompat
import com.thebluealliance.android.R
import com.thebluealliance.android.domain.getShortLabel
import com.thebluealliance.android.domain.model.Match
import com.thebluealliance.android.messaging.NotificationChannelManager
import java.util.Date
import java.text.DateFormat

object MatchTrackingNotificationBuilder {

    const val NOTIFICATION_ID = 7175 // "TBA Team 175" :)

    private fun stopPendingIntent(context: Context): PendingIntent {
        val stopIntent = Intent(context, MatchTrackingService::class.java).apply {
            action = MatchTrackingService.ACTION_STOP
        }
        return PendingIntent.getService(
            context, 0, stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }

    fun build(
        context: Context,
        state: TrackedTeamState,
        teamAvatar: Bitmap? = null,
    ): Notification {
        val teamNumber = state.teamKey.removePrefix("frc")
        val title = buildTitle(teamNumber, state)
        val body = buildBody(state)
        val chipText = buildChipText(state)

        val builder = NotificationCompat.Builder(context, NotificationChannelManager.CHANNEL_TRACKING)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(body.lines().firstOrNull() ?: "")
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .addAction(R.drawable.ic_notification, "Stop tracking", stopPendingIntent(context))

        if (teamAvatar != null) {
            builder.setLargeIcon(teamAvatar)
        }

        // API 35+: Request promoted ongoing notification
        if (Build.VERSION.SDK_INT >= 35) {
            builder.setRequestPromotedOngoing(true)
        }

        // API 36+: Set chip text for status bar
        if (Build.VERSION.SDK_INT >= 36 && chipText != null) {
            builder.setShortCriticalText(chipText)
        }

        return builder.build()
    }

    private fun buildTitle(teamNumber: String, state: TrackedTeamState): String {
        return if (state.isTeamPlaying && state.currentMatch != null) {
            "$teamNumber — NOW PLAYING ${state.currentMatch.getShortLabel(state.playoffType)}"
        } else if (state.nextMatch != null) {
            val timeStr = formatMatchTime(state.nextMatch)
            "$teamNumber — Next: ${state.nextMatch.getShortLabel(state.playoffType)}${timeStr}"
        } else if (state.record != null) {
            "$teamNumber — Quals complete (${state.record})"
        } else {
            "$teamNumber — Tracking"
        }
    }

    private fun buildBody(state: TrackedTeamState): String {
        val lines = mutableListOf<String>()

        // Next match — skip if the team is in the NOW match (NOW already conveys "what's next")
        if (state.nextMatch != null && !state.isTeamPlaying) {
            val timeStr = formatMatchTime(state.nextMatch)
            val teams = formatTeams(state.nextMatch, state.teamKey)
            lines.add("Next: ${state.nextMatch.getShortLabel(state.playoffType)}${timeStr}\n\u21B3 $teams")
        }

        // Current match at event
        if (state.currentMatch != null) {
            val teams = formatTeams(state.currentMatch, state.teamKey)
            lines.add("Now: ${state.currentMatch.getShortLabel(state.playoffType)}\n\u21B3 $teams")
        }

        // Last completed match for this team
        if (state.lastMatch != null) {
            val teams = formatTeams(state.lastMatch, state.teamKey)
            val score = "${state.lastMatch.redScore}-${state.lastMatch.blueScore}"
            lines.add("Last: ${state.lastMatch.getShortLabel(state.playoffType)} - Score $score\n\u21B3 $teams")
        }

        return lines.joinToString("\n").ifEmpty { "Waiting for match data..." }
    }

    private fun buildChipText(state: TrackedTeamState): String? {
        if (state.isTeamPlaying && state.currentMatch != null) {
            return state.currentMatch.getShortLabel(state.playoffType)
        }
        if (state.nextMatch != null) {
            val name = state.nextMatch.getShortLabel(state.playoffType)
            val time = state.nextMatch.predictedTime ?: state.nextMatch.time
            if (time != null) {
                val fmt = DateFormat.getTimeInstance(DateFormat.SHORT)
                val timeStr = fmt.format(Date(time * 1000))
                return "$name $timeStr"
            }
            return name
        }
        return null
    }

    fun buildDormant(context: Context, teamKey: String): Notification {
        val teamNumber = teamKey.removePrefix("frc")

        return NotificationCompat.Builder(context, NotificationChannelManager.CHANNEL_TRACKING)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("$teamNumber — Tracking paused")
            .setContentText("Will resume when matches are scheduled")
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .addAction(R.drawable.ic_notification, "Stop tracking", stopPendingIntent(context))
            .build()
    }

    private fun formatMatchTime(match: Match): String {
        val predicted = match.predictedTime
        val scheduled = match.time
        return when {
            predicted != null -> {
                val fmt = DateFormat.getTimeInstance(DateFormat.SHORT)
                " est. ${fmt.format(Date(predicted * 1000))}"
            }
            scheduled != null -> {
                val fmt = DateFormat.getTimeInstance(DateFormat.SHORT)
                " ${fmt.format(Date(scheduled * 1000))}"
            }
            else -> ""
        }
    }

    private fun formatTeams(match: Match, trackedTeamKey: String): String {
        fun formatTeam(key: String): String {
            val num = key.removePrefix("frc")
            return if (key == trackedTeamKey) "*$num*" else num
        }
        val red = match.redTeamKeys.joinToString(", ") { formatTeam(it) }
        val blue = match.blueTeamKeys.joinToString(", ") { formatTeam(it) }
        return "$red vs $blue"
    }
}
