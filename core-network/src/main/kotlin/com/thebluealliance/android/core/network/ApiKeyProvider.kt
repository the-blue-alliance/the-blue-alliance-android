package com.thebluealliance.android.core.network

import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.edit
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 * Canonical resolver for the TBA APIv3 read key, shared by `:app`, `:wear` and `:tv`
 * (RFC 0004 network unification). Previously each module shipped a near-identical copy; the
 * Wear Remote Config race (commit c2f6df2ce) was exactly the class of bug that triplication
 * invites, so the logic now lives in one place.
 *
 * Resolution order:
 *  1. [bakedApiKey] when [isDebug] — the build-time key from `local.properties`.
 *  2. the key cached in [prefs] from a previous Remote Config fetch.
 *  3. a fresh Firebase Remote Config value, waiting up to [REMOTE_FETCH_TIMEOUT_SECONDS]s for
 *     the in-flight fetch kicked off by [init] so the first call on a clean install isn't
 *     keyless.
 *  4. [bakedApiKey] as a last resort (empty in shipped releases).
 *
 * The module-specific inputs ([isDebug], [bakedApiKey]) are passed in rather than read from a
 * `BuildConfig` so this can live in `:core-network`, which has no app `BuildConfig`. Each
 * module supplies its own [prefs] so the cached key keeps living exactly where it always has —
 * no migration or forced re-fetch on upgrade.
 */
class ApiKeyProvider(
    private val isDebug: Boolean,
    private val bakedApiKey: String,
    private val prefs: SharedPreferences,
    private val remoteConfig: FirebaseRemoteConfig,
) {
    private val fetchComplete = CountDownLatch(1)

    val apiKey: String
        get() {
            if (isDebug) return bakedApiKey

            val cached = prefs.getString(PREFS_KEY, null)
            if (!cached.isNullOrEmpty()) return cached

            // No cached key — wait for the in-flight Remote Config fetch so the first network
            // call on a fresh install doesn't go out with an empty key.
            fetchComplete.await(REMOTE_FETCH_TIMEOUT_SECONDS, TimeUnit.SECONDS)

            val remoteKey = remoteConfig.getString(REMOTE_CONFIG_KEY)
            if (remoteKey.isNotEmpty()) return remoteKey

            return bakedApiKey
        }

    /** Kick off the Remote Config fetch once, at app startup, and cache the result. */
    fun init() {
        if (isDebug) {
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
        private const val REMOTE_FETCH_TIMEOUT_SECONDS = 5L
    }
}
