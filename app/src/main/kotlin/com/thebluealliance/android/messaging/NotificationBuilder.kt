package com.thebluealliance.android.messaging

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.RemoteMessage
import com.thebluealliance.android.MainActivity
import com.thebluealliance.android.R
import com.thebluealliance.android.domain.model.NotificationType
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationBuilder @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    fun buildFromRemoteMessage(message: RemoteMessage): Notification? {
        val data = message.data
        val typeKey = data["notification_type"] ?: return null
        val type = NotificationType.fromServerKey(typeKey) ?: return null
        if (type.isSilent) return null

        val title = message.notification?.title ?: return null
        val body = message.notification?.body ?: ""

        return build(
            channelId = type.channelId,
            title = title,
            body = body,
            eventKey = data["event_key"],
            matchKey = data["match_key"],
            teamKey = data["team_key"],
            notificationType = typeKey,
        )
    }

    fun build(
        channelId: String,
        title: String,
        body: String,
        eventKey: String? = null,
        matchKey: String? = null,
        teamKey: String? = null,
        notificationType: String? = null,
    ): Notification {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            notificationType?.let { putExtra(EXTRA_NOTIFICATION_TYPE, it) }
            eventKey?.let { putExtra(EXTRA_EVENT_KEY, it) }
            matchKey?.let { putExtra(EXTRA_MATCH_KEY, it) }
            teamKey?.let { putExtra(EXTRA_TEAM_KEY, it) }
        }

        val requestCode = (eventKey ?: matchKey ?: teamKey ?: "").hashCode()
        val pendingIntent = PendingIntent.getActivity(
            context, requestCode, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        return NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()
    }

    companion object {
        const val EXTRA_NOTIFICATION_TYPE = "notification_type"
        const val EXTRA_EVENT_KEY = "event_key"
        const val EXTRA_MATCH_KEY = "match_key"
        const val EXTRA_TEAM_KEY = "team_key"
    }
}
