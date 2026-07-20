package com.thebluealliance.android.wear.complication

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

/**
 * Per-complication SharedPreferences storage for complication display data.
 * Each complication instance gets its own preference file keyed by complication ID.
 * The tracked team number is stored in [TeamTrackerPreferences] (single source of truth).
 */
class TeamTrackingComplicationPreferences(
    context: Context,
    complicationId: Int,
) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("complication_$complicationId", Context.MODE_PRIVATE)

    /** Non-null while a [beginBatch] batch is open; setters write here instead of committing. */
    private var batchEditor: SharedPreferences.Editor? = null

    /** Route a write to the open [beginBatch] batch, or commit it immediately when none is active. */
    private inline fun put(write: SharedPreferences.Editor.() -> Unit) {
        val editor = batchEditor
        if (editor != null) editor.write() else prefs.edit { write() }
    }

    /** Open a batch: subsequent property writes accumulate until [applyBatch]. */
    fun beginBatch() {
        if (batchEditor == null) batchEditor = prefs.edit()
    }

    /** Commit and close the batch opened by [beginBatch] as a single write, if one is open. */
    fun applyBatch() {
        batchEditor?.apply()
        batchEditor = null
    }

    var matchLabel: String
        get() = prefs.getString(KEY_MATCH_LABEL, "") ?: ""
        set(value) = put { putString(KEY_MATCH_LABEL, value) }

    var matchTime: String
        get() = prefs.getString(KEY_MATCH_TIME, "") ?: ""
        set(value) = put { putString(KEY_MATCH_TIME, value) }

    var avatarBase64: String?
        get() = prefs.getString(KEY_AVATAR_BASE64, null)
        set(value) = put { putString(KEY_AVATAR_BASE64, value) }

    var activeEventName: String
        get() = prefs.getString(KEY_ACTIVE_EVENT_NAME, "") ?: ""
        set(value) = put { putString(KEY_ACTIVE_EVENT_NAME, value) }

    var upcomingEventName: String
        get() = prefs.getString(KEY_UPCOMING_EVENT_NAME, "") ?: ""
        set(value) = put { putString(KEY_UPCOMING_EVENT_NAME, value) }

    var upcomingEventDate: String
        get() = prefs.getString(KEY_UPCOMING_EVENT_DATE, "") ?: ""
        set(value) = put { putString(KEY_UPCOMING_EVENT_DATE, value) }

    fun clear() {
        prefs.edit { clear() }
    }

    companion object {
        private const val KEY_MATCH_LABEL = "match_label"
        private const val KEY_MATCH_TIME = "match_time"
        private const val KEY_AVATAR_BASE64 = "avatar_base64"
        private const val KEY_ACTIVE_EVENT_NAME = "active_event_name"
        private const val KEY_UPCOMING_EVENT_NAME = "upcoming_event_name"
        private const val KEY_UPCOMING_EVENT_DATE = "upcoming_event_date"

        /** Global preferences (not per-complication) for tracking active complication IDs. */
        fun globalPrefs(context: Context): SharedPreferences =
            context.getSharedPreferences("complication_global", Context.MODE_PRIVATE)

        fun getActiveComplicationIds(context: Context): Set<Int> =
            globalPrefs(context)
                .getStringSet("active_ids", emptySet())
                ?.mapNotNull { it.toIntOrNull() }
                ?.toSet() ?: emptySet()

        fun addComplicationId(
            context: Context,
            id: Int,
        ) {
            val prefs = globalPrefs(context)
            val ids = prefs.getStringSet("active_ids", emptySet())?.toMutableSet() ?: mutableSetOf()
            ids.add(id.toString())
            prefs.edit { putStringSet("active_ids", ids) }
        }

        fun removeComplicationId(
            context: Context,
            id: Int,
        ) {
            val prefs = globalPrefs(context)
            val ids = prefs.getStringSet("active_ids", emptySet())?.toMutableSet() ?: mutableSetOf()
            ids.remove(id.toString())
            prefs.edit { putStringSet("active_ids", ids) }
        }
    }
}
