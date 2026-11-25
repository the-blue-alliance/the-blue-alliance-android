package com.thebluealliance.android.ui.components

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp

/**
 * Width a table of weighted columns needs: the width its weights were tuned for. Wider, its columns
 * spread too far apart to scan.
 */
val TableNaturalWidth = 720.dp

/** Where a table sits in its window: [start] inset from the start edge, [width] wide. */
internal data class TableSpan(
    val start: Dp,
    val width: Dp,
)

/**
 * Places a table that needs [naturalWidth] in [available]: at the reading column's start edge, as
 * wide as it needs but no wider than the column. Windows narrower than both fill with it.
 */
internal fun tableSpan(
    available: Dp,
    naturalWidth: Dp = TableNaturalWidth,
): TableSpan =
    TableSpan(
        start = readingInset(available),
        width = minOf(available, ReadingMaxWidth, naturalWidth),
    )

/**
 * Lays a table out [naturalWidth] wide, capped at the reading column and starting at its start
 * edge, so every table shares one start edge without stretching. Apply it to a table's header,
 * each of its rows and their dividers, with the same [naturalWidth], so they share one width; a
 * window narrower than the table fills with it.
 */
fun Modifier.tableWidth(naturalWidth: Dp = TableNaturalWidth): Modifier =
    layout { measurable, constraints ->
        if (!constraints.hasBoundedWidth) {
            val placeable = measurable.measure(constraints)
            return@layout layout(
                placeable.width,
                placeable.height,
            ) { placeable.placeRelative(0, 0) }
        }
        val span = tableSpan(constraints.maxWidth.toDp(), naturalWidth)
        val start = span.start.roundToPx()
        val width = span.width.roundToPx().coerceAtMost(constraints.maxWidth - start)
        val placeable = measurable.measure(constraints.copy(minWidth = width, maxWidth = width))
        layout(constraints.maxWidth, placeable.height) { placeable.placeRelative(start, 0) }
    }

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

/**
 * Content width of the part of a table row that scrolls sideways, or null when [columnWidths] plus
 * [rowPadding] on each side fit [viewport]. The first [frozenCount] columns and the start padding
 * stay put; the scrolling part holds the remaining columns and the end padding. Only the rows
 * scroll; anything else in the table, such as expanded details, stays at the viewport's width.
 */
internal fun sideScrollRowWidth(
    viewport: Float,
    columnWidths: List<Float>?,
    rowPadding: Float,
    frozenCount: Int,
): Float? =
    columnWidths
        ?.takeIf { it.sum() + rowPadding * 2 > viewport }
        ?.let { it.drop(frozenCount).sum() + rowPadding }

/** Width of the edge shadow that hints a sideways-scrolling row has more columns past an edge. */
private val SIDE_SCROLL_CUE = 16.dp

/**
 * Lays a table row out [rowWidth] wide and scrolls it sideways with [state], which the header and
 * every row share so the columns move together. While more columns lie past either edge, that edge
 * shades into [cueColor], visible even where the edge falls in a gap between columns. Drags that
 * run past either edge stop there rather than carrying on to an enclosing pager.
 */
fun Modifier.tableSideScroll(
    state: ScrollState,
    rowWidth: Dp,
    cueColor: Color,
): Modifier =
    this
        .fillMaxWidth()
        .drawWithContent {
            drawContent()
            val cue = SIDE_SCROLL_CUE.toPx()
            val ltr = layoutDirection == LayoutDirection.Ltr
            // Scroll-forward columns lie past the end edge: the right in LTR, the left in RTL.
            if (state.canScrollForward) drawEdgeCue(cueColor, cue, atRight = ltr)
            if (state.canScrollBackward) drawEdgeCue(cueColor, cue, atRight = !ltr)
        }.nestedScroll(KeepSideScrollLeftover)
        .horizontalScroll(state)
        .width(rowWidth)

/**
 * Lets sideways drags anywhere on a row, including its frozen cells, scroll [state] like the part
 * under [tableSideScroll] does, so they never turn an enclosing pager instead.
 */
fun Modifier.tableSideDrag(
    state: ScrollState,
    layoutDirection: LayoutDirection,
): Modifier =
    this
        .nestedScroll(KeepSideScrollLeftover)
        .scrollable(
            state,
            Orientation.Horizontal,
            reverseDirection =
                ScrollableDefaults.reverseDirection(
                    layoutDirection,
                    Orientation.Horizontal,
                    reverseScrolling = false,
                ),
        )

/** Shades a [width]-wide strip at one edge, from transparent inward to [color] at the edge. */
private fun DrawScope.drawEdgeCue(
    color: Color,
    width: Float,
    atRight: Boolean,
) {
    val left = if (atRight) size.width - width else 0f
    drawRect(
        brush =
            Brush.horizontalGradient(
                colors =
                    if (atRight) {
                        listOf(
                            Color.Transparent,
                            color,
                        )
                    } else {
                        listOf(color, Color.Transparent)
                    },
                startX = left,
                endX = left + width,
            ),
        topLeft = Offset(left, 0f),
        size = Size(width, size.height),
    )
}

/**
 * Keeps sideways drag and fling left over at a row's scroll edge from reaching an enclosing pager,
 * which would otherwise turn the page mid-gesture.
 */
private object KeepSideScrollLeftover : NestedScrollConnection {
    override fun onPostScroll(
        consumed: Offset,
        available: Offset,
        source: NestedScrollSource,
    ): Offset = Offset(available.x, 0f)

    override suspend fun onPostFling(
        consumed: Velocity,
        available: Velocity,
    ): Velocity = Velocity(available.x, 0f)
}
