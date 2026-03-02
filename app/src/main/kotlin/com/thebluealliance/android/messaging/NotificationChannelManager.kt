package com.thebluealliance.android.messaging

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationChannelManager @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    companion object {
        const val CHANNEL_MATCH = "match_alerts"
        const val CHANNEL_EVENT = "event_updates"
        const val CHANNEL_GENERAL = "general"
        const val CHANNEL_TRACKING = "match_tracking"
    }

    fun createChannels() {
        val manager = context.getSystemService(NotificationManager::class.java)
        manager.createNotificationChannels(
            listOf(
                NotificationChannel(
                    CHANNEL_MATCH,
                    "Match Alerts",
                    NotificationManager.IMPORTANCE_HIGH,
                ).apply {
                    description = "Upcoming matches, scores, and match videos"
                },
                NotificationChannel(
                    CHANNEL_EVENT,
                    "Event Updates",
                    NotificationManager.IMPORTANCE_DEFAULT,
                ).apply {
                    description = "Schedule changes, alliances, awards, and competition level updates"
                },
                NotificationChannel(
                    CHANNEL_GENERAL,
                    "General",
                    NotificationManager.IMPORTANCE_DEFAULT,
                ).apply {
                    description = "Test notifications and broadcasts"
                },
                NotificationChannel(
                    CHANNEL_TRACKING,
                    "Match Tracking",
                    NotificationManager.IMPORTANCE_DEFAULT,
                ).apply {
                    description = "Live match tracking for your favorite team at an event"
                },
            )
        )
    }
}
