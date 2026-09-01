package com.thebluealliance.android.messaging

import android.content.SharedPreferences
import androidx.core.content.edit
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Stable per-install id the clientapi uses as the myTBA device key. Independent of push,
 * so myTBA writes keep working on distributions with no push transport.
 */
@Singleton
class DeviceIdProvider
    @Inject
    constructor(
        private val sharedPreferences: SharedPreferences,
    ) {
        val deviceUuid: String
            get() {
                val existing = sharedPreferences.getString(PREF_DEVICE_UUID, null)
                if (existing != null) return existing
                val uuid = UUID.randomUUID().toString()
                sharedPreferences.edit { putString(PREF_DEVICE_UUID, uuid) }
                return uuid
            }

        private companion object {
            const val PREF_DEVICE_UUID = "device_uuid"
        }
    }
