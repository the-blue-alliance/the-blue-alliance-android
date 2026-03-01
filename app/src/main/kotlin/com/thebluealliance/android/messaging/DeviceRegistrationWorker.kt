package com.thebluealliance.android.messaging

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.thebluealliance.android.data.remote.ClientApi
import com.thebluealliance.android.data.remote.dto.RegisterDeviceRequestDto
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class DeviceRegistrationWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val clientApi: ClientApi,
    private val sharedPreferences: SharedPreferences,
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        const val KEY_FCM_TOKEN = "fcm_token"
        const val KEY_DEVICE_UUID = "device_uuid"
        private const val TAG = "DeviceRegWorker"
    }

    override suspend fun doWork(): Result {
        val token = inputData.getString(KEY_FCM_TOKEN) ?: return Result.failure()
        val deviceUuid = inputData.getString(KEY_DEVICE_UUID) ?: return Result.failure()

        return try {
            clientApi.registerDevice(
                RegisterDeviceRequestDto(
                    mobileId = token,
                    name = Build.MODEL,
                    deviceUuid = deviceUuid,
                )
            )
            sharedPreferences.edit().putString(DeviceRegistrationManager.PREF_REGISTERED_TOKEN, token).apply()
            Log.d(TAG, "Registered device with token ${token.take(10)}...")
            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Registration failed, will retry", e)
            Result.retry()
        }
    }
}
