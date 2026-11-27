package com.thebluealliance.android.widget

import androidx.datastore.preferences.core.MutablePreferences
import java.time.Instant
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

/** How a refresh's outcome is recorded in the widget's state, and how its age is shown. */
internal object WidgetRefreshState {
    /**
     * Record a refresh where the API couldn't be reached. Today the worker falls through to the
     * empty-cache path: the match rows are cleared and the widget says "No upcoming events".
     */
    fun markFailed(prefs: MutablePreferences) {
        prefs.remove(TeamTrackingWidgetKeys.EVENT_KEY)
        prefs[TeamTrackingWidgetKeys.EVENT_NAME] = ""
        prefs[TeamTrackingWidgetKeys.RECORD] = ""
        prefs.remove(TeamTrackingWidgetKeys.UPCOMING_EVENTS)
        TeamTrackingWidgetKeys.ALL_LAST_MATCH_KEYS.forEach { prefs.remove(it) }
        TeamTrackingWidgetKeys.ALL_NEXT_MATCH_KEYS.forEach { prefs.remove(it) }
        prefs[TeamTrackingWidgetKeys.WIDGET_STATE] = WidgetState.NO_UPCOMING.value
    }

    /** Record a refresh that fetched fresh data at [nowMillis]. */
    fun markSucceeded(
        prefs: MutablePreferences,
        nowMillis: Long,
    ) {
        prefs[TeamTrackingWidgetKeys.LAST_SUCCESS_AT] = nowMillis
    }

    /** The footer: "Updated 7:53 AM". Null hides it. */
    @Suppress("UNUSED_PARAMETER")
    fun footerText(
        lastSuccessAtMillis: Long?,
        refreshFailed: Boolean,
        now: ZonedDateTime,
        use24Hour: Boolean,
        locale: Locale,
    ): String? {
        val updated = Instant.ofEpochMilli(lastSuccessAtMillis ?: return null).atZone(now.zone)
        val pattern = if (use24Hour) "HH:mm" else "h:mm a"
        return "Updated ${updated.format(DateTimeFormatter.ofPattern(pattern, locale))}"
    }
}
