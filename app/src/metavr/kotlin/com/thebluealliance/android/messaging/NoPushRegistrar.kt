package com.thebluealliance.android.messaging

/**
 * Horizon OS has no Google Play services and therefore no FCM, and Meta's User
 * Notifications can't carry per-user match data. There is nothing to register, so myTBA
 * relies on pulling on open.
 */
class NoPushRegistrar : PushRegistrar {
    override val isPushAvailable = false

    override suspend fun registerIfNeeded() = Unit

    override suspend fun onSignIn() = Unit

    override suspend fun onSignOut() = Unit
}
