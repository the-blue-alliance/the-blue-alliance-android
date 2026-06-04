package com.thebluealliance.android.wear.data

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.edit
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.thebluealliance.android.wear.BuildConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApiKeyProvider
    @Inject
    constructor(
        @param:ApplicationContext private val context: Context,
        private val remoteConfig: FirebaseRemoteConfig,
    ) {
        private val prefs: SharedPreferences =
            context.getSharedPreferences("api_config", Context.MODE_PRIVATE)

        private val fetchComplete = CountDownLatch(1)

        val apiKey: String
            get() {
                if (BuildConfig.DEBUG) return BuildConfig.TBA_API_KEY

                val cached = prefs.getString(PREFS_KEY, null)
                if (!cached.isNullOrEmpty()) return cached

                // No cached key — wait for the in-flight Remote Config fetch so the first
                // network call on a fresh install doesn't go out with an empty key.
                fetchComplete.await(5, TimeUnit.SECONDS)

                val remoteKey = remoteConfig.getString(REMOTE_CONFIG_KEY)
                if (remoteKey.isNotEmpty()) return remoteKey

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
                        prefs.edit { putString(PREFS_KEY, key) }
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
