package com.thebluealliance.android.messaging

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
                    try { myTBARepository.refreshFavorites() } catch (_: Exception) {}
                }
                return
            }
            NotificationType.UPDATE_SUBSCRIPTIONS -> {
                scope.launch {
                    try { myTBARepository.refreshSubscriptions() } catch (_: Exception) {}
                }
                return
            }
            else -> {}
        }

        // Build and show display notification
        val notification = notificationBuilder.buildFromRemoteMessage(message) ?: return
        val manager = getSystemService(NotificationManager::class.java)
        val notificationId = message.data.hashCode()
        manager.notify(notificationId, notification)
    }

    companion object {
        private const val TAG = "TBAMessaging"
    }
}
