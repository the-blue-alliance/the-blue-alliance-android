package com.thebluealliance.android.wear.complication

import android.content.Context
import android.content.SharedPreferences

/**
 * Per-complication SharedPreferences storage for complication data.
 * Each complication instance gets its own preference file keyed by complication ID.
 */
class TeamTrackingComplicationPreferences(context: Context, complicationId: Int) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("complication_$complicationId", Context.MODE_PRIVATE)

    var teamNumber: String
        get() = prefs.getString(KEY_TEAM_NUMBER, "") ?: ""
        set(value) = prefs.edit().putString(KEY_TEAM_NUMBER, value).apply()

    var matchLabel: String
        get() = prefs.getString(KEY_MATCH_LABEL, "") ?: ""
        set(value) = prefs.edit().putString(KEY_MATCH_LABEL, value).apply()

    var matchTime: String
        get() = prefs.getString(KEY_MATCH_TIME, "") ?: ""
        set(value) = prefs.edit().putString(KEY_MATCH_TIME, value).apply()

    var avatarBase64: String?
        get() = prefs.getString(KEY_AVATAR_BASE64, null)
        set(value) = prefs.edit().putString(KEY_AVATAR_BASE64, value).apply()

    var hasActiveEvent: Boolean
        get() = prefs.getBoolean(KEY_HAS_ACTIVE_EVENT, false)
        set(value) = prefs.edit().putBoolean(KEY_HAS_ACTIVE_EVENT, value).apply()

    var activeEventName: String
        get() = prefs.getString(KEY_ACTIVE_EVENT_NAME, "") ?: ""
        set(value) = prefs.edit().putString(KEY_ACTIVE_EVENT_NAME, value).apply()

    var upcomingEventName: String
        get() = prefs.getString(KEY_UPCOMING_EVENT_NAME, "") ?: ""
        set(value) = prefs.edit().putString(KEY_UPCOMING_EVENT_NAME, value).apply()

    var upcomingEventDate: String
        get() = prefs.getString(KEY_UPCOMING_EVENT_DATE, "") ?: ""
        set(value) = prefs.edit().putString(KEY_UPCOMING_EVENT_DATE, value).apply()

    fun clear() {
        prefs.edit().clear().apply()
    }

    companion object {
        private const val KEY_TEAM_NUMBER = "team_number"
        private const val KEY_MATCH_LABEL = "match_label"
        private const val KEY_MATCH_TIME = "match_time"
        private const val KEY_AVATAR_BASE64 = "avatar_base64"
        private const val KEY_HAS_ACTIVE_EVENT = "has_active_event"
        private const val KEY_ACTIVE_EVENT_NAME = "active_event_name"
        private const val KEY_UPCOMING_EVENT_NAME = "upcoming_event_name"
        private const val KEY_UPCOMING_EVENT_DATE = "upcoming_event_date"

        /** Global preferences (not per-complication) for tracking active complication IDs. */
        fun globalPrefs(context: Context): SharedPreferences =
            context.getSharedPreferences("complication_global", Context.MODE_PRIVATE)

        fun getActiveComplicationIds(context: Context): Set<Int> =
            globalPrefs(context).getStringSet("active_ids", emptySet())
                ?.mapNotNull { it.toIntOrNull() }?.toSet() ?: emptySet()

        fun addComplicationId(context: Context, id: Int) {
            val prefs = globalPrefs(context)
            val ids = prefs.getStringSet("active_ids", emptySet())?.toMutableSet() ?: mutableSetOf()
            ids.add(id.toString())
            prefs.edit().putStringSet("active_ids", ids).apply()
        }

        fun removeComplicationId(context: Context, id: Int) {
            val prefs = globalPrefs(context)
            val ids = prefs.getStringSet("active_ids", emptySet())?.toMutableSet() ?: mutableSetOf()
            ids.remove(id.toString())
            prefs.edit().putStringSet("active_ids", ids).apply()
        }
    }
}
