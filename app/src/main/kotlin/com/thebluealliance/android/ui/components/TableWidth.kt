package com.thebluealliance.android.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Widest a data table grows; beyond it, weighted columns spread too far apart to scan. */
val TABLE_MAX_WIDTH = 720.dp

/**
 * Fills the available width up to [TABLE_MAX_WIDTH], centered. Apply it to a table's header and
 * to each of its rows so they share one column grid; it is a no-op on narrower windows.
 */
fun Modifier.tableWidth(): Modifier =
    this
        .fillMaxWidth()
        .wrapContentWidth(Alignment.CenterHorizontally)
        .widthIn(max = TABLE_MAX_WIDTH)
        .fillMaxWidth()

/** Side inset that centers a [TABLE_MAX_WIDTH] column in [availableWidth]; zero when narrower. */
internal fun tableInset(availableWidth: Dp): Dp =
    ((availableWidth - TABLE_MAX_WIDTH) / 2).coerceAtLeast(0.dp)

/**
 * Column widths for a table of [available] width whose columns are [weights] shares but must be at
 * least [minWidths] wide, e.g. so a header word never breaks mid-word. Null when the plain weights
 * already fit, so the table lays out exactly as weighted. Otherwise short columns get their
 * minimum and the rest share what remains by weight; when the minimums exceed [available], the
 * widths are the minimums and the table needs to scroll.
 */
internal fun fitColumnWidths(
    available: Float,
    weights: List<Float>,
    minWidths: List<Float>,
): List<Float>? {
    require(weights.size == minWidths.size)
    val totalWeight = weights.sum()
    if (weights.indices.all { available * weights[it] / totalWeight >= minWidths[it] }) return null
    if (minWidths.sum() >= available) return minWidths
    val pinned = BooleanArray(weights.size)
    while (true) {
        val remaining =
            available - minWidths.indices.filter { pinned[it] }.sumOf { minWidths[it].toDouble() }
        val freeWeight = weights.indices.filter { !pinned[it] }.sumOf { weights[it].toDouble() }
        val widths =
            weights.indices.map {
                if (pinned[it]) minWidths[it] else (remaining * weights[it] / freeWeight).toFloat()
            }
        val short = weights.indices.filter { !pinned[it] && widths[it] < minWidths[it] }
        if (short.isEmpty()) return widths
        short.forEach { pinned[it] = true }
    }
}
