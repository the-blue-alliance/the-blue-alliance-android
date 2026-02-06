package com.thebluealliance.android.config

import android.content.SharedPreferences
import android.util.Log
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.thebluealliance.android.BuildConfig
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApiKeyProvider @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    private val remoteConfig: FirebaseRemoteConfig,
) {
    private val fetchComplete = CountDownLatch(1)

    val apiKey: String
        get() {
            if (BuildConfig.DEBUG) {
                return BuildConfig.TBA_API_KEY
            }

            // Use cached key immediately if available
            val cachedKey = sharedPreferences.getString(PREFS_KEY, null)
            if (!cachedKey.isNullOrEmpty()) {
                return cachedKey
            }

            // No cached key — wait for the in-flight Remote Config fetch
            fetchComplete.await(5, TimeUnit.SECONDS)

            // Check Remote Config (now activated)
            val remoteConfigKey = remoteConfig.getString(REMOTE_CONFIG_KEY)
            if (remoteConfigKey.isNotEmpty()) {
                return remoteConfigKey
            }

            // Last resort (will be empty string if not in local.properties)
            return BuildConfig.TBA_API_KEY
        }

    fun init() {
        if (BuildConfig.DEBUG) {
            fetchComplete.countDown()
            return
        }

        remoteConfig.fetchAndActivate().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val key = remoteConfig.getString(REMOTE_CONFIG_KEY)
                if (key.isNotEmpty()) {
                    sharedPreferences.edit().putString(PREFS_KEY, key).apply()
                    Log.d(TAG, "Updated API key from Remote Config")
                }
            } else {
                Log.w(TAG, "Failed to fetch Remote Config", task.exception)
            }
            fetchComplete.countDown()
        }
    }

    companion object {
        private const val TAG = "ApiKeyProvider"
        private const val REMOTE_CONFIG_KEY = "apiv3_auth_key"
        private const val PREFS_KEY = "tba_api_key"
    }
}
