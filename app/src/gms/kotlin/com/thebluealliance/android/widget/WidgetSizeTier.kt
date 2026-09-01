package com.thebluealliance.android.widget

import androidx.compose.ui.unit.Dp

/**
 * Single source of truth for the widget's size-tier classification, shared by the renderer
 * ([TeamTrackingWidget], which classifies the snapped `LocalSize`) and the metrics rollup
 * ([WidgetMetricsWorker], which classifies the portrait box from `getAppWidgetOptions`). The
 * two inputs can disagree slightly at breakpoint boundaries — fine for an approximate tier
 * dimension.
 */
object WidgetSizeTier {
    /** Tier when the launcher hasn't reported size options yet (empty options Bundle). */
    const val UNKNOWN = "unknown"

    fun classify(
        widthDp: Int,
        heightDp: Int,
    ): String =
        when {
            widthDp >= 250 && heightDp >= 110 -> "full"
            widthDp >= 250 -> "compact"
            widthDp >= 110 && heightDp >= 110 -> "square"
            widthDp >= 110 -> "minimal"
            else -> "tiny"
        }

    // floor() preserves the `>=` integer-threshold semantics exactly (x >= T <=> floor(x) >= T
    // for integer T, x >= 0), so this matches the renderer's Dp comparisons.
    fun classify(
        width: Dp,
        height: Dp,
    ): String = classify(width.value.toInt(), height.value.toInt())
}
