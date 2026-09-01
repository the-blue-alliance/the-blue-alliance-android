package com.thebluealliance.android.messaging

/**
 * Registers this device with the TBA clientapi so myTBA can push to it.
 *
 * The transport is distribution-specific: the `gms` flavor registers an FCM token, while
 * Horizon OS has no Google Play services and therefore no push at all, so its
 * implementation is a no-op. myTBA still syncs by pulling on open either way.
 */
interface PushRegistrar {
    /** False when this build has no push transport, so there is nothing to register. */
    val isPushAvailable: Boolean

    /** Registers on app start if signed in and the stored registration is stale. */
    suspend fun registerIfNeeded()

    suspend fun onSignIn()

    suspend fun onSignOut()
}
