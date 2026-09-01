package com.thebluealliance.android.messaging

import android.content.SharedPreferences
import android.os.Build
import android.util.Log
import androidx.core.content.edit
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.google.firebase.messaging.FirebaseMessaging
import com.thebluealliance.android.data.remote.ClientApi
import com.thebluealliance.android.data.remote.dto.RegisterDeviceRequestDto
import com.thebluealliance.android.data.repository.AuthRepository
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/** [PushRegistrar] backed by Firebase Cloud Messaging, which requires Google Play services. */
@Singleton
class DeviceRegistrationManager
    @Inject
    constructor(
        private val clientApi: ClientApi,
        private val authRepository: AuthRepository,
        private val sharedPreferences: SharedPreferences,
        private val workManager: WorkManager,
        private val deviceIdProvider: DeviceIdProvider,
    ) : PushRegistrar {
        companion object {
            private const val TAG = "DeviceRegistration"
            internal const val PREF_REGISTERED_TOKEN = "registered_fcm_token"
        }

        override val isPushAvailable = true

        override suspend fun registerIfNeeded() {
            if (!authRepository.isSignedIn()) return
            try {
                val token = FirebaseMessaging.getInstance().token.await()
                val lastRegistered = sharedPreferences.getString(PREF_REGISTERED_TOKEN, null)
                if (token == lastRegistered) return
                register(token)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to get FCM token", e)
            }
        }

        suspend fun onNewToken(token: String) {
            if (!authRepository.isSignedIn()) return
            register(token)
        }

        override suspend fun onSignIn() {
            try {
                val token = FirebaseMessaging.getInstance().token.await()
                register(token)
            } catch (e: Exception) {
                Log.e(TAG, "Registration failed after sign-in", e)
            }
        }

        override suspend fun onSignOut() {
            try {
                val token = sharedPreferences.getString(PREF_REGISTERED_TOKEN, null)
                if (token != null) {
                    clientApi.unregisterDevice(
                        RegisterDeviceRequestDto(
                            mobileId = token,
                            name = Build.MODEL,
                            deviceUuid = deviceIdProvider.deviceUuid,
                        ),
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Unregister failed", e)
            } finally {
                sharedPreferences.edit { remove(PREF_REGISTERED_TOKEN) }
            }
        }

        private suspend fun register(token: String) {
            try {
                clientApi.registerDevice(
                    RegisterDeviceRequestDto(
                        mobileId = token,
                        name = Build.MODEL,
                        deviceUuid = deviceIdProvider.deviceUuid,
                    ),
                )
                sharedPreferences.edit { putString(PREF_REGISTERED_TOKEN, token) }
                Log.d(TAG, "Registered device with token ${token.take(10)}...")
            } catch (e: Exception) {
                Log.e(TAG, "Registration failed, scheduling retry", e)
                enqueueRetry(token)
            }
        }

        private fun enqueueRetry(token: String) {
            val request =
                OneTimeWorkRequestBuilder<DeviceRegistrationWorker>()
                    .setInputData(
                        workDataOf(
                            DeviceRegistrationWorker.KEY_FCM_TOKEN to token,
                            DeviceRegistrationWorker.KEY_DEVICE_UUID to
                                deviceIdProvider.deviceUuid,
                        ),
                    ).setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 30, TimeUnit.SECONDS)
                    .setConstraints(
                        Constraints
                            .Builder()
                            .setRequiredNetworkType(NetworkType.CONNECTED)
                            .build(),
                    ).build()

            workManager.enqueueUniqueWork(
                "device_registration",
                ExistingWorkPolicy.REPLACE,
                request,
            )
            Log.d(TAG, "Enqueued device registration retry worker")
        }
    }
