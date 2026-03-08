package com.thebluealliance.android.widget

import androidx.datastore.preferences.core.stringPreferencesKey

object TeamTrackingWidgetKeys {
    val TEAM_NUMBER = stringPreferencesKey("team_number")
    val TEAM_KEY = stringPreferencesKey("team_key")
    val TEAM_NICKNAME = stringPreferencesKey("team_nickname")
    val AVATAR_BASE64 = stringPreferencesKey("avatar_base64")
    val NEXT_ALLIANCE = stringPreferencesKey("next_alliance")
    val EVENT_KEY = stringPreferencesKey("event_key")
    val EVENT_NAME = stringPreferencesKey("event_name")
    val RECORD = stringPreferencesKey("record")
    val LAST_UPDATED = stringPreferencesKey("last_updated")

    // Last match
    val LAST_MATCH_LABEL = stringPreferencesKey("last_match_label")
    val LAST_MATCH_RED_TEAMS = stringPreferencesKey("last_match_red_teams")
    val LAST_MATCH_BLUE_TEAMS = stringPreferencesKey("last_match_blue_teams")
    val LAST_MATCH_RED_SCORE = stringPreferencesKey("last_match_red_score")
    val LAST_MATCH_BLUE_SCORE = stringPreferencesKey("last_match_blue_score")
    val LAST_MATCH_WINNING_ALLIANCE = stringPreferencesKey("last_match_winning_alliance")
    val LAST_MATCH_RED_RP = stringPreferencesKey("last_match_red_rp")
    val LAST_MATCH_BLUE_RP = stringPreferencesKey("last_match_blue_rp")

    // Next match
    val NEXT_MATCH_LABEL = stringPreferencesKey("next_match_label")
    val NEXT_MATCH_RED_TEAMS = stringPreferencesKey("next_match_red_teams")
    val NEXT_MATCH_BLUE_TEAMS = stringPreferencesKey("next_match_blue_teams")
    val NEXT_MATCH_TIME = stringPreferencesKey("next_match_time")
    val NEXT_MATCH_TIME_IS_ESTIMATE = stringPreferencesKey("next_match_time_is_estimate")

    // Upcoming events (when no current event) — tab-separated rows: "name\tcity\tdate"
    val UPCOMING_EVENTS = stringPreferencesKey("upcoming_events")

    val ALL_LAST_MATCH_KEYS = listOf(
        LAST_MATCH_LABEL, LAST_MATCH_RED_TEAMS, LAST_MATCH_BLUE_TEAMS,
        LAST_MATCH_RED_SCORE, LAST_MATCH_BLUE_SCORE, LAST_MATCH_WINNING_ALLIANCE,
        LAST_MATCH_RED_RP, LAST_MATCH_BLUE_RP,
    )
    val ALL_NEXT_MATCH_KEYS = listOf(
        NEXT_MATCH_LABEL, NEXT_MATCH_RED_TEAMS, NEXT_MATCH_BLUE_TEAMS,
        NEXT_MATCH_TIME, NEXT_MATCH_TIME_IS_ESTIMATE,
    )
}
