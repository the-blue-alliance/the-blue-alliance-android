package com.thebluealliance.android.wear.data

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.thebluealliance.android.wear.BuildConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApiKeyProvider @Inject constructor(
    @ApplicationContext private val context: Context,
    private val remoteConfig: FirebaseRemoteConfig,
) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("api_config", Context.MODE_PRIVATE)

    val apiKey: String
        get() {
            if (BuildConfig.DEBUG) return BuildConfig.TBA_API_KEY

            val cached = prefs.getString(PREFS_KEY, null)
            if (!cached.isNullOrEmpty()) return cached

            val remoteKey = remoteConfig.getString(REMOTE_CONFIG_KEY)
            if (remoteKey.isNotEmpty()) return remoteKey

            return BuildConfig.TBA_API_KEY
        }

    fun init() {
        if (BuildConfig.DEBUG) return

        remoteConfig.fetchAndActivate().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val key = remoteConfig.getString(REMOTE_CONFIG_KEY)
                if (key.isNotEmpty()) {
                    prefs.edit().putString(PREFS_KEY, key).apply()
                    Log.d(TAG, "Updated API key from Remote Config")
                }
            } else {
                Log.w(TAG, "Failed to fetch Remote Config", task.exception)
            }
        }
    }

    companion object {
        private const val TAG = "ApiKeyProvider"
        private const val REMOTE_CONFIG_KEY = "apiv3_auth_key"
        private const val PREFS_KEY = "tba_api_key"
    }
}
