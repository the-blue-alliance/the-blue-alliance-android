package com.thebluealliance.android.wear.tracker

import android.content.Context
import android.content.SharedPreferences

/**
 * App-level SharedPreferences for the tracked team.
 * Single source of truth for team number — complications read from here too.
 * Per-complication prefs ([TeamTrackingComplicationPreferences]) only store display data.
 */
class TeamTrackerPreferences(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("team_tracker_app", Context.MODE_PRIVATE)

    var teamNumber: String
        get() = prefs.getString(KEY_TEAM_NUMBER, "") ?: ""
        set(value) = prefs.edit().putString(KEY_TEAM_NUMBER, value).apply()

    var teamNickname: String
        get() = prefs.getString(KEY_TEAM_NICKNAME, "") ?: ""
        set(value) = prefs.edit().putString(KEY_TEAM_NICKNAME, value).apply()

    var avatarBase64: String?
        get() = prefs.getString(KEY_AVATAR_BASE64, null)
        set(value) = prefs.edit().putString(KEY_AVATAR_BASE64, value).apply()

    var eventName: String
        get() = prefs.getString(KEY_EVENT_NAME, "") ?: ""
        set(value) = prefs.edit().putString(KEY_EVENT_NAME, value).apply()

    var record: String
        get() = prefs.getString(KEY_RECORD, "") ?: ""
        set(value) = prefs.edit().putString(KEY_RECORD, value).apply()

    var hasActiveEvent: Boolean
        get() = prefs.getBoolean(KEY_HAS_ACTIVE_EVENT, false)
        set(value) = prefs.edit().putBoolean(KEY_HAS_ACTIVE_EVENT, value).apply()

    // Last match

    var lastMatchLabel: String
        get() = prefs.getString(KEY_LAST_MATCH_LABEL, "") ?: ""
        set(value) = prefs.edit().putString(KEY_LAST_MATCH_LABEL, value).apply()

    var lastMatchRedTeams: String
        get() = prefs.getString(KEY_LAST_MATCH_RED_TEAMS, "") ?: ""
        set(value) = prefs.edit().putString(KEY_LAST_MATCH_RED_TEAMS, value).apply()

    var lastMatchBlueTeams: String
        get() = prefs.getString(KEY_LAST_MATCH_BLUE_TEAMS, "") ?: ""
        set(value) = prefs.edit().putString(KEY_LAST_MATCH_BLUE_TEAMS, value).apply()

    var lastMatchRedScore: Int
        get() = prefs.getInt(KEY_LAST_MATCH_RED_SCORE, -1)
        set(value) = prefs.edit().putInt(KEY_LAST_MATCH_RED_SCORE, value).apply()

    var lastMatchBlueScore: Int
        get() = prefs.getInt(KEY_LAST_MATCH_BLUE_SCORE, -1)
        set(value) = prefs.edit().putInt(KEY_LAST_MATCH_BLUE_SCORE, value).apply()

    var lastMatchWinningAlliance: String
        get() = prefs.getString(KEY_LAST_MATCH_WINNING_ALLIANCE, "") ?: ""
        set(value) = prefs.edit().putString(KEY_LAST_MATCH_WINNING_ALLIANCE, value).apply()

    var lastAlliance: String
        get() = prefs.getString(KEY_LAST_ALLIANCE, "") ?: ""
        set(value) = prefs.edit().putString(KEY_LAST_ALLIANCE, value).apply()

    var lastMatchBonusRp: Int
        get() = prefs.getInt(KEY_LAST_MATCH_BONUS_RP, 0)
        set(value) = prefs.edit().putInt(KEY_LAST_MATCH_BONUS_RP, value).apply()

    // Next match

    var nextMatchLabel: String
        get() = prefs.getString(KEY_NEXT_MATCH_LABEL, "") ?: ""
        set(value) = prefs.edit().putString(KEY_NEXT_MATCH_LABEL, value).apply()

    var nextMatchRedTeams: String
        get() = prefs.getString(KEY_NEXT_MATCH_RED_TEAMS, "") ?: ""
        set(value) = prefs.edit().putString(KEY_NEXT_MATCH_RED_TEAMS, value).apply()

    var nextMatchBlueTeams: String
        get() = prefs.getString(KEY_NEXT_MATCH_BLUE_TEAMS, "") ?: ""
        set(value) = prefs.edit().putString(KEY_NEXT_MATCH_BLUE_TEAMS, value).apply()

    var nextMatchTime: String
        get() = prefs.getString(KEY_NEXT_MATCH_TIME, "") ?: ""
        set(value) = prefs.edit().putString(KEY_NEXT_MATCH_TIME, value).apply()

    var nextMatchTimeIsEstimate: Boolean
        get() = prefs.getBoolean(KEY_NEXT_MATCH_TIME_IS_ESTIMATE, false)
        set(value) = prefs.edit().putBoolean(KEY_NEXT_MATCH_TIME_IS_ESTIMATE, value).apply()

    var nextAlliance: String
        get() = prefs.getString(KEY_NEXT_ALLIANCE, "") ?: ""
        set(value) = prefs.edit().putString(KEY_NEXT_ALLIANCE, value).apply()

    // Upcoming event (when no active event)

    /** Pipe-separated list of "Date — Name" strings for all upcoming events. */
    var upcomingEvents: String
        get() = prefs.getString(KEY_UPCOMING_EVENTS, "") ?: ""
        set(value) = prefs.edit().putString(KEY_UPCOMING_EVENTS, value).apply()

    fun registerOnChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        prefs.registerOnSharedPreferenceChangeListener(listener)
    }

    fun unregisterOnChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        prefs.unregisterOnSharedPreferenceChangeListener(listener)
    }

    fun clear() {
        prefs.edit().clear().apply()
    }

    companion object {
        private const val KEY_TEAM_NUMBER = "team_number"
        private const val KEY_TEAM_NICKNAME = "team_nickname"
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
