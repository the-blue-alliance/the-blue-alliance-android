package com.thebluealliance.android.messaging

import android.app.Notification
import android.app.NotificationManager
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.thebluealliance.android.data.repository.MyTBARepository
import com.thebluealliance.android.domain.model.NotificationType
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class TBAFirebaseMessagingService : FirebaseMessagingService() {
    @Inject lateinit var deviceRegistrationManager: DeviceRegistrationManager

    @Inject lateinit var notificationBuilder: NotificationBuilder

    @Inject lateinit var myTBARepository: MyTBARepository

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onNewToken(token: String) {
        Log.d(TAG, "New FCM token: ${token.take(10)}...")
        scope.launch { deviceRegistrationManager.onNewToken(token) }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        val typeKey = message.data["notification_type"]
        Log.d(TAG, "Received notification: type=$typeKey")

        val type = typeKey?.let { NotificationType.fromServerKey(it) }

        // Handle silent sync notifications
        when (type) {
            NotificationType.UPDATE_FAVORITES -> {
                scope.launch {
                    try {
                        myTBARepository.refreshFavorites()
                    } catch (_: Exception) {
                    }
                }
                return
            }
            NotificationType.UPDATE_SUBSCRIPTIONS -> {
                scope.launch {
                    try {
                        myTBARepository.refreshSubscriptions()
                    } catch (_: Exception) {
                    }
                }
                return
            }
            else -> {}
        }

        // Build and show display notification
        val notification = buildDisplayNotification(message, type) ?: return
        val manager = getSystemService(NotificationManager::class.java)
        if (!manager.areNotificationsEnabled()) {
            // notify() would silently no-op; make the drop diagnosable.
            Log.w(TAG, "Dropping notification (type=$typeKey): POST_NOTIFICATIONS not granted")
            return
        }
        val notificationId = NotificationBuilder.collapseId(message.data)
        manager.notify(notificationId, notification)
    }

    private fun buildDisplayNotification(
        message: RemoteMessage,
        type: NotificationType?,
    ): Notification? {
        if (type == null || type.isSilent) return null
        val data = message.data
        val title = message.notification?.title ?: return null
        return notificationBuilder.build(
            channelId = type.channelId,
            title = title,
            body = message.notification?.body ?: "",
            eventKey = data["event_key"],
            matchKey = data["match_key"],
            teamKey = data["team_key"],
            notificationType = data["notification_type"],
        )
    }

    companion object {
        private const val TAG = "TBAMessaging"
    }
}
