package com.thebluealliance.android.wear.tracker

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

/**
 * App-level SharedPreferences for the tracked team.
 * Single source of truth for team number — complications read from here too.
 * Per-complication prefs ([TeamTrackingComplicationPreferences]) only store display data.
 */
class TeamTrackerPreferences(
    context: Context,
) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("team_tracker_app", Context.MODE_PRIVATE)

    /** Non-null while a [beginBatch] batch is open; setters write here instead of committing. */
    private var batchEditor: SharedPreferences.Editor? = null

    /** Route a write to the open [beginBatch] batch, or commit it immediately when none is active. */
    private inline fun put(write: SharedPreferences.Editor.() -> Unit) {
        val editor = batchEditor
        if (editor != null) editor.write() else prefs.edit { write() }
    }

    /**
     * Open a batch: subsequent property writes accumulate into one editor instead of each
     * committing on its own. Pairs with [applyBatch]. Without this a single worker refresh fires
     * ~20 writes, each waking a full reload + recompose in the foreground tracker. Not reentrant;
     * intended for the single refresh worker.
     */
    fun beginBatch() {
        if (batchEditor == null) batchEditor = prefs.edit()
    }

    /** Commit and close the batch opened by [beginBatch] as a single write, if one is open. */
    fun applyBatch() {
        batchEditor?.apply()
        batchEditor = null
    }

    var teamNumber: String
        get() = prefs.getString(KEY_TEAM_NUMBER, "") ?: ""
        set(value) = put { putString(KEY_TEAM_NUMBER, value) }

    var isLoading: Boolean
        get() = prefs.getBoolean(KEY_IS_LOADING, false)
        set(value) = put { putBoolean(KEY_IS_LOADING, value) }

    /** True when the most recent refresh threw before completing (network/API failure). */
    var lastRefreshFailed: Boolean
        get() = prefs.getBoolean(KEY_LAST_REFRESH_FAILED, false)
        set(value) = put { putBoolean(KEY_LAST_REFRESH_FAILED, value) }
    var avatarBase64: String?
        get() = prefs.getString(KEY_AVATAR_BASE64, null)
        set(value) = put { putString(KEY_AVATAR_BASE64, value) }

    var eventName: String
        get() = prefs.getString(KEY_EVENT_NAME, "") ?: ""
        set(value) = put { putString(KEY_EVENT_NAME, value) }

    var record: String
        get() = prefs.getString(KEY_RECORD, "") ?: ""
        set(value) = put { putString(KEY_RECORD, value) }

    var hasActiveEvent: Boolean
        get() = prefs.getBoolean(KEY_HAS_ACTIVE_EVENT, false)
        set(value) = put { putBoolean(KEY_HAS_ACTIVE_EVENT, value) }

    // Last match

    var lastMatchLabel: String
        get() = prefs.getString(KEY_LAST_MATCH_LABEL, "") ?: ""
        set(value) = put { putString(KEY_LAST_MATCH_LABEL, value) }

    var lastMatchRedTeams: String
        get() = prefs.getString(KEY_LAST_MATCH_RED_TEAMS, "") ?: ""
        set(value) = put { putString(KEY_LAST_MATCH_RED_TEAMS, value) }

    var lastMatchBlueTeams: String
        get() = prefs.getString(KEY_LAST_MATCH_BLUE_TEAMS, "") ?: ""
        set(value) = put { putString(KEY_LAST_MATCH_BLUE_TEAMS, value) }

    var lastMatchRedScore: Int
        get() = prefs.getInt(KEY_LAST_MATCH_RED_SCORE, -1)
        set(value) = put { putInt(KEY_LAST_MATCH_RED_SCORE, value) }

    var lastMatchBlueScore: Int
        get() = prefs.getInt(KEY_LAST_MATCH_BLUE_SCORE, -1)
        set(value) = put { putInt(KEY_LAST_MATCH_BLUE_SCORE, value) }

    var lastMatchWinningAlliance: String
        get() = prefs.getString(KEY_LAST_MATCH_WINNING_ALLIANCE, "") ?: ""
        set(value) = put { putString(KEY_LAST_MATCH_WINNING_ALLIANCE, value) }

    var lastAlliance: String
        get() = prefs.getString(KEY_LAST_ALLIANCE, "") ?: ""
        set(value) = put { putString(KEY_LAST_ALLIANCE, value) }

    var lastMatchBonusRp: Int
        get() = prefs.getInt(KEY_LAST_MATCH_BONUS_RP, 0)
        set(value) = put { putInt(KEY_LAST_MATCH_BONUS_RP, value) }

    // Next match

    var nextMatchLabel: String
        get() = prefs.getString(KEY_NEXT_MATCH_LABEL, "") ?: ""
        set(value) = put { putString(KEY_NEXT_MATCH_LABEL, value) }

    var nextMatchRedTeams: String
        get() = prefs.getString(KEY_NEXT_MATCH_RED_TEAMS, "") ?: ""
        set(value) = put { putString(KEY_NEXT_MATCH_RED_TEAMS, value) }

    var nextMatchBlueTeams: String
        get() = prefs.getString(KEY_NEXT_MATCH_BLUE_TEAMS, "") ?: ""
        set(value) = put { putString(KEY_NEXT_MATCH_BLUE_TEAMS, value) }

    var nextMatchTime: String
        get() = prefs.getString(KEY_NEXT_MATCH_TIME, "") ?: ""
        set(value) = put { putString(KEY_NEXT_MATCH_TIME, value) }

    var nextMatchTimeIsEstimate: Boolean
        get() = prefs.getBoolean(KEY_NEXT_MATCH_TIME_IS_ESTIMATE, false)
        set(value) = put { putBoolean(KEY_NEXT_MATCH_TIME_IS_ESTIMATE, value) }

    var nextAlliance: String
        get() = prefs.getString(KEY_NEXT_ALLIANCE, "") ?: ""
        set(value) = put { putString(KEY_NEXT_ALLIANCE, value) }

    // Upcoming event (when no active event)

    /** Pipe-separated list of "Date — Name" strings for all upcoming events. */
    var upcomingEvents: String
        get() = prefs.getString(KEY_UPCOMING_EVENTS, "") ?: ""
        set(value) = put { putString(KEY_UPCOMING_EVENTS, value) }

    fun registerOnChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        prefs.registerOnSharedPreferenceChangeListener(listener)
    }

    fun unregisterOnChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        prefs.unregisterOnSharedPreferenceChangeListener(listener)
    }

    /** Clear all cached data except the team number, and mark as loading. */
    fun clearCachedData() {
        val team = teamNumber
        prefs.edit {
            clear()
            putString(KEY_TEAM_NUMBER, team)
            putBoolean(KEY_IS_LOADING, true)
        }
    }

    fun clear() {
        prefs.edit { clear() }
    }

    companion object {
        private const val KEY_TEAM_NUMBER = "team_number"
        private const val KEY_IS_LOADING = "is_loading"
        private const val KEY_LAST_REFRESH_FAILED = "last_refresh_failed"
        private const val KEY_AVATAR_BASE64 = "avatar_base64"
        private const val KEY_EVENT_NAME = "event_name"
        private const val KEY_RECORD = "record"
        private const val KEY_HAS_ACTIVE_EVENT = "has_active_event"
        private const val KEY_LAST_MATCH_LABEL = "last_match_label"
        private const val KEY_LAST_MATCH_RED_TEAMS = "last_match_red_teams"
        private const val KEY_LAST_MATCH_BLUE_TEAMS = "last_match_blue_teams"
        private const val KEY_LAST_MATCH_RED_SCORE = "last_match_red_score"
        private const val KEY_LAST_MATCH_BLUE_SCORE = "last_match_blue_score"
        private const val KEY_LAST_MATCH_WINNING_ALLIANCE = "last_match_winning_alliance"
        private const val KEY_LAST_ALLIANCE = "last_alliance"
        private const val KEY_LAST_MATCH_BONUS_RP = "last_match_bonus_rp"
        private const val KEY_NEXT_MATCH_LABEL = "next_match_label"
        private const val KEY_NEXT_MATCH_RED_TEAMS = "next_match_red_teams"
        private const val KEY_NEXT_MATCH_BLUE_TEAMS = "next_match_blue_teams"
        private const val KEY_NEXT_MATCH_TIME = "next_match_time"
        private const val KEY_NEXT_MATCH_TIME_IS_ESTIMATE = "next_match_time_is_estimate"
        private const val KEY_NEXT_ALLIANCE = "next_alliance"
        private const val KEY_UPCOMING_EVENTS = "upcoming_events"
    }
}
