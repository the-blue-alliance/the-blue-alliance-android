package com.thebluealliance.android.messaging

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import com.google.firebase.messaging.RemoteMessage
import com.thebluealliance.android.MainActivity
import com.thebluealliance.android.R
import com.thebluealliance.android.domain.model.NotificationType
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationBuilder
    @Inject
    constructor(
        @param:ApplicationContext private val context: Context,
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
            val intent =
                Intent(context, MainActivity::class.java).apply {
                    // NEW_TASK + CLEAR_TASK routes the tap through onCreate (with the
                    // synthetic back stack) even when the app is already running —
                    // SINGLE_TOP landed in onNewIntent, which dropped the tap. Same
                    // pattern as TeamTrackingWidgetOpenAction.
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    // Unique data URI so PendingIntents for different notifications never
                    // compare filterEquals-equal (extras don't count), which previously
                    // let FLAG_UPDATE_CURRENT rewrite an older notification's target.
                    data =
                        "tba://notification/$notificationType/$eventKey/$matchKey/$teamKey".toUri()
                    notificationType?.let { putExtra(EXTRA_NOTIFICATION_TYPE, it) }
                    eventKey?.let { putExtra(EXTRA_EVENT_KEY, it) }
                    matchKey?.let { putExtra(EXTRA_MATCH_KEY, it) }
                    teamKey?.let { putExtra(EXTRA_TEAM_KEY, it) }
                }

            val requestCode =
                listOf(notificationType, eventKey, matchKey, teamKey).joinToString("|").hashCode()
            val pendingIntent =
                PendingIntent.getActivity(
                    context,
                    requestCode,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
                )

            return NotificationCompat
                .Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(body)
                .setStyle(NotificationCompat.BigTextStyle().bigText(body))
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                // Collapsing fan-out pushes share a notify id; alert once so the replacing push
                // updates silently instead of re-buzzing for content already shown (#1461).
                .setOnlyAlertOnce(true)
                .build()
        }

        companion object {
            const val EXTRA_NOTIFICATION_TYPE = "notification_type"
            const val EXTRA_EVENT_KEY = "event_key"
            const val EXTRA_MATCH_KEY = "match_key"
            const val EXTRA_TEAM_KEY = "team_key"

            /**
             * Notification id for [android.app.NotificationManager.notify]. Match notifications
             * collapse per (type, match) so two *followed teams in the same match* show ONE
             * notification instead of two — the backend fans out one push per subscribed team, so
             * the later push replaces the earlier rather than stacking (#1461). Notifications
             * without a match context keep a per-message id (no over-collapsing).
             */
            fun collapseId(data: Map<String, String>): Int {
                val matchKey = data["match_key"]
                return if (!matchKey.isNullOrEmpty()) {
                    "${data["notification_type"]}|$matchKey".hashCode()
                } else {
                    // Event-level types (alliance_selection, schedule_updated, …) can still double
                    // for users following 2+ teams at one event; collapsing those needs per-type
                    // granularity (e.g. comp_level) and is left as a follow-up.
                    data.hashCode()
                }
            }
        }
    }
