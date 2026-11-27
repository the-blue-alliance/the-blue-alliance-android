package com.thebluealliance.android.widget

import androidx.datastore.preferences.core.MutablePreferences
import java.time.Instant
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

/** How a refresh's outcome is recorded in the widget's state, and how its age is shown. */
internal object WidgetRefreshState {
    /**
     * Record a refresh where the API couldn't be reached. Whatever the widget last showed stays,
     * along with when it was fetched, and team info cached on the device is filled in; otherwise
     * only the failure is noted. A widget that has never loaded
     * anything switches to [WidgetState.LOAD_FAILED] instead of claiming "No upcoming events".
     */
    fun markFailed(
        prefs: MutablePreferences,
        cachedNickname: String? = null,
        cachedAvatarBase64: String? = null,
    ) {
        prefs[TeamTrackingWidgetKeys.REFRESH_FAILED] = true
        // Team info already in the local database is still good; only fill in, never clear.
        if (!cachedNickname.isNullOrBlank()) {
            prefs[TeamTrackingWidgetKeys.TEAM_NICKNAME] = cachedNickname
        }
        if (cachedAvatarBase64 != null) {
            prefs[TeamTrackingWidgetKeys.AVATAR_BASE64] = cachedAvatarBase64
        }
        val everLoaded =
            prefs[TeamTrackingWidgetKeys.LAST_SUCCESS_AT] != null ||
                prefs[TeamTrackingWidgetKeys.LAST_UPDATED] != null
        if (!everLoaded) {
            prefs[TeamTrackingWidgetKeys.WIDGET_STATE] = WidgetState.LOAD_FAILED.value
        }
    }

    /**
     * Point the widget at [teamNumber]. Switching teams drops the previous team's data, so a
     * failed first refresh can't show it under the new team's name. Saving the same team keeps
     * everything, including its widget state.
     */
    fun setTeam(
        prefs: MutablePreferences,
        teamNumber: String,
    ) {
        val teamKey = "frc$teamNumber"
        if (prefs[TeamTrackingWidgetKeys.TEAM_KEY] != teamKey) {
            prefs.remove(TeamTrackingWidgetKeys.TEAM_NICKNAME)
            prefs.remove(TeamTrackingWidgetKeys.AVATAR_BASE64)
            prefs.remove(TeamTrackingWidgetKeys.EVENT_KEY)
            prefs.remove(TeamTrackingWidgetKeys.EVENT_NAME)
            prefs.remove(TeamTrackingWidgetKeys.RECORD)
            prefs.remove(TeamTrackingWidgetKeys.NEXT_ALLIANCE)
            prefs.remove(TeamTrackingWidgetKeys.UPCOMING_EVENTS)
            TeamTrackingWidgetKeys.ALL_LAST_MATCH_KEYS.forEach { prefs.remove(it) }
            TeamTrackingWidgetKeys.ALL_NEXT_MATCH_KEYS.forEach { prefs.remove(it) }
            prefs.remove(TeamTrackingWidgetKeys.LAST_SUCCESS_AT)
            prefs.remove(TeamTrackingWidgetKeys.LAST_UPDATED)
            prefs.remove(TeamTrackingWidgetKeys.REFRESH_FAILED)
            prefs.remove(TeamTrackingWidgetKeys.WIDGET_STATE)
        }
        // Sane default until the first worker run stamps the real state — shrinks the
        // "unknown" widget_state window for analytics.
        if (prefs[TeamTrackingWidgetKeys.WIDGET_STATE] == null) {
            prefs[TeamTrackingWidgetKeys.WIDGET_STATE] = WidgetState.NO_UPCOMING.value
        }
        prefs[TeamTrackingWidgetKeys.TEAM_NUMBER] = teamNumber
        prefs[TeamTrackingWidgetKeys.TEAM_KEY] = teamKey
    }

    /** Record a refresh that fetched fresh data at [nowMillis]. */
    fun markSucceeded(
        prefs: MutablePreferences,
        nowMillis: Long,
    ) {
        prefs[TeamTrackingWidgetKeys.LAST_SUCCESS_AT] = nowMillis
        prefs.remove(TeamTrackingWidgetKeys.REFRESH_FAILED)
        // Superseded by LAST_SUCCESS_AT, which is formatted when the widget renders.
        prefs.remove(TeamTrackingWidgetKeys.LAST_UPDATED)
    }

    /**
     * The footer: "Updated 7:53 AM", "Updated Tue 7:53 AM" within the week, "Updated Mar 9" after
     * that, and "Couldn't
     * refresh · updated 7:53 AM" after a failed refresh. Null hides it.
     */
    fun footerText(
        lastSuccessAtMillis: Long?,
        refreshFailed: Boolean,
        now: ZonedDateTime,
        use24Hour: Boolean,
        locale: Locale,
    ): String? {
        if (lastSuccessAtMillis == null) {
            return if (refreshFailed) "Couldn't load · tap to retry" else null
        }
        val updated = Instant.ofEpochMilli(lastSuccessAtMillis).atZone(now.zone)
        val time = if (use24Hour) "HH:mm" else "h:mm a"
        val pattern =
            when (ChronoUnit.DAYS.between(updated.toLocalDate(), now.toLocalDate())) {
                0L -> time
                in 1L..6L -> "EEE $time"
                else -> "MMM d"
            }
        val stamp = updated.format(DateTimeFormatter.ofPattern(pattern, locale))
        return if (refreshFailed) "Couldn't refresh · updated $stamp" else "Updated $stamp"
    }
}
