package com.thebluealliance.android.widget

import android.content.Context
import android.util.Log
import androidx.glance.GlanceId
import androidx.glance.appwidget.state.getAppWidgetState
import androidx.glance.state.PreferencesGlanceStateDefinition
import com.google.firebase.Firebase
import com.google.firebase.analytics.analytics
import com.google.firebase.analytics.logEvent

/** One widget's aggregate engagement over a query window, read from `AppWidgetEvent`. */
data class WidgetEventSnapshot(
    val appWidgetId: Int,
    val visibleMillis: Long,
)

/** A folded `widget_engagement` row, one per (state x size-tier) group. */
data class EngagementGroup(
    val widgetState: String,
    val tier: String,
    val visibleWindowCount: Int,
    val impressionMillis: Long,
)

/**
 * Firebase Analytics for the Team Tracking widget — clicks (in-process, every device) and the
 * daily engagement rollup (views, QPR2+ only; see [WidgetMetricsWorker]). No PII: no team key,
 * no appWidgetId, no user id leaves the device.
 */
object WidgetAnalytics {
    const val EVENT_WIDGET_CLICK = "widget_click"
    const val EVENT_WIDGET_ENGAGEMENT = "widget_engagement"

    const val PARAM_SURFACE = "surface"
    const val PARAM_WIDGET_STATE = "widget_state"
    const val PARAM_WIDGET_SIZE_TIER = "widget_size_tier"

    // An AppWidgetEvent is a reporting window (~hourly, OEM-tunable), NOT a discrete view, so this
    // is a coarse activity count; impression_ms (summed visible time) is the primary view signal.
    // Per-tap clicks come from widget_click (every tap, all devices), not from here.
    const val PARAM_VISIBLE_WINDOW_COUNT = "visible_window_count"
    const val PARAM_IMPRESSION_MS = "impression_ms"

    const val SURFACE_OPEN = "open"
    const val SURFACE_SETTINGS = "settings"
    const val SURFACE_REFRESH = "refresh"

    /** `widget_state` when the worker hasn't stamped a state yet (freshly added widget). */
    const val STATE_UNKNOWN = "unknown"

    private const val TAG = "WidgetAnalytics"

    /**
     * Logs a `widget_click` for a tap on [surface], tagged with the widget's current
     * [WidgetState]. Runs on every device/Android version (no platform-API dependency).
     */
    suspend fun logWidgetClick(
        context: Context,
        surface: String,
        glanceId: GlanceId,
    ) {
        val widgetState =
            try {
                val state = getAppWidgetState(context, PreferencesGlanceStateDefinition, glanceId)
                state[TeamTrackingWidgetKeys.WIDGET_STATE] ?: STATE_UNKNOWN
            } catch (e: Exception) {
                Log.w(TAG, "Failed to read widget state for click", e)
                STATE_UNKNOWN
            }
        Firebase.analytics.logEvent(EVENT_WIDGET_CLICK) {
            param(PARAM_SURFACE, surface)
            param(PARAM_WIDGET_STATE, widgetState)
        }
    }

    /**
     * Pure fold of per-widget snapshots into one [EngagementGroup] per (state x tier).
     * [stateTierFor] maps an `appWidgetId` to its (widget_state, tier); a null result drops the
     * snapshot (the widget is gone). No Android dependency — unit-tested directly.
     */
    fun foldEngagement(
        snapshots: List<WidgetEventSnapshot>,
        stateTierFor: (appWidgetId: Int) -> Pair<String, String>?,
    ): List<EngagementGroup> {
        // accumulator per group: [visibleWindowCount, impressionMillis]
        val acc = LinkedHashMap<Pair<String, String>, LongArray>()
        for (s in snapshots) {
            val key = stateTierFor(s.appWidgetId) ?: continue
            val a = acc.getOrPut(key) { LongArray(2) }
            a[0] += 1
            a[1] += s.visibleMillis
        }
        return acc.map { (key, a) ->
            EngagementGroup(
                widgetState = key.first,
                tier = key.second,
                visibleWindowCount = a[0].toInt(),
                impressionMillis = a[1],
            )
        }
    }
}
