package com.thebluealliance.android.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Widest a column of prose or detail content grows before it centers. */
val ReadingMaxWidth = 840.dp

/**
 * Widest a running paragraph grows inside the reading column, for comfortable line lengths. No
 * narrower than a compact window, so phone layout is unchanged.
 */
val ProseMaxWidth = 600.dp

/** Widest a label-and-value stat row grows on a wide panel, so the value stays near its label. */
val StatRowMaxWidth = 360.dp

/** Widest a stat row can be and still fill it; no phone in portrait is wider. */
private val StatRowFillMaxWidth = 600.dp

/** Width a stat row lays out in [available]: all of it up to compact widths, else capped. */
internal fun statRowWidth(available: Dp): Dp =
    if (available <= StatRowFillMaxWidth) available else StatRowMaxWidth

/**
 * Caps a label-and-value row at [StatRowMaxWidth] once it has more than a compact window's width,
 * so phones lay out as without it.
 */
fun Modifier.statRowWidth(): Modifier =
    layout { measurable, constraints ->
        val max =
            if (constraints.hasBoundedWidth) {
                statRowWidth(constraints.maxWidth.toDp()).roundToPx()
            } else {
                constraints.maxWidth
            }
        val placeable =
            measurable.measure(
                constraints.copy(minWidth = minOf(constraints.minWidth, max), maxWidth = max),
            )
        layout(placeable.width, placeable.height) { placeable.placeRelative(0, 0) }
    }

/**
 * Centers the content and caps it at [ReadingMaxWidth], so lines stay readable on wide windows.
 * Windows narrower than the cap lay out exactly as without it. For scrolling lists use
 * [ReadingWidthBox] instead, so the whole width still scrolls.
 */
fun Modifier.readingWidth(): Modifier =
    fillMaxWidth()
        .wrapContentWidth(Alignment.CenterHorizontally)
        .widthIn(max = ReadingMaxWidth)

/**
 * Widest a control row, such as a segmented button row or a full-width button, grows once the
 * reading column is at its full width, so its segments stay a comfortable size.
 */
val ControlMaxWidth = 400.dp

/**
 * Width a control row lays out in [available], where [fullColumn] is the width it gets in a reading
 * column at [ReadingMaxWidth]: all of it while the column is narrower, else [ControlMaxWidth].
 */
internal fun controlWidth(
    available: Dp,
    fullColumn: Dp,
): Dp = if (available < fullColumn) available else ControlMaxWidth

/**
 * Caps a control row at [ControlMaxWidth] once its reading column, inset [columnPadding] on each
 * side, has stopped growing, so any window up to the reading width lays out as without it.
 */
fun Modifier.controlWidth(columnPadding: Dp): Modifier =
    layout { measurable, constraints ->
        val max =
            if (constraints.hasBoundedWidth) {
                controlWidth(
                    constraints.maxWidth.toDp(),
                    ReadingMaxWidth - columnPadding * 2,
                ).roundToPx()
            } else {
                constraints.maxWidth
            }
        val placeable =
            measurable.measure(
                constraints.copy(minWidth = minOf(constraints.minWidth, max), maxWidth = max),
            )
        layout(placeable.width, placeable.height) { placeable.placeRelative(0, 0) }
    }

/**
 * Where a field in a top bar sits so it shares the reading column's edges: [start] is its offset
 * from the start of its slot and [maxWidth] the most it may grow, both in pixels.
 */
internal data class ReadingColumnFit(
    val start: Float,
    val maxWidth: Float,
)

/**
 * Fits a field whose slot begins at [fieldStart] (left edge, or right edge in RTL) to the reading
 * column of a content area from [areaLeft] to [areaRight], all in the same pixel coordinates. The
 * field starts no earlier than the column's start edge and ends no later than its end edge. Where
 * the column fills the area, its edges are the window's, so the field is as wide as its slot.
 */
internal fun readingColumnFit(
    fieldStart: Float,
    areaLeft: Float,
    areaRight: Float,
    column: Float,
    rtl: Boolean,
): ReadingColumnFit {
    val inset = sheetInset(areaRight - areaLeft, column).coerceAtLeast(0f)
    val columnLeft = areaLeft + inset
    val columnRight = areaRight - inset
    return if (rtl) {
        val start = (fieldStart - columnRight).coerceAtLeast(0f)
        ReadingColumnFit(start, (fieldStart - start - columnLeft).coerceAtLeast(0f))
    } else {
        val start = (columnLeft - fieldStart).coerceAtLeast(0f)
        ReadingColumnFit(start, (columnRight - fieldStart - start).coerceAtLeast(0f))
    }
}

/** Side inset that centers the reading column in [availableWidth]; zero when narrower. */
internal fun readingInset(availableWidth: Dp): Dp =
    ((availableWidth - ReadingMaxWidth) / 2).coerceAtLeast(0.dp)

/**
 * Lays out a full-width scrolling list whose content is centered at [ReadingMaxWidth]: [content]
 * receives [contentPadding] widened by the side inset. Dragging in the side margins still scrolls.
 */
@Composable
fun ReadingWidthBox(
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
    content: @Composable (PaddingValues) -> Unit,
) {
    BoxWithConstraints(modifier) {
        val inset = readingInset(this.maxWidth)
        val direction = LocalLayoutDirection.current
        val density = LocalDensity.current
        val bleed =
            with(density) {
                bandBleed(
                    constraints.maxWidth.toFloat(),
                    ReadingMaxWidth.toPx(),
                    MinSheetMargin.toPx(),
                ).toDp()
            }
        CompositionLocalProvider(LocalReadingBandBleed provides bleed) {
            content(
                PaddingValues(
                    start = contentPadding.calculateStartPadding(direction) + inset,
                    top = contentPadding.calculateTopPadding(),
                    end = contentPadding.calculateEndPadding(direction) + inset,
                    bottom = contentPadding.calculateBottomPadding(),
                ),
            )
        }
    }
}

/**
 * How far a band in a [ReadingWidthBox] list paints past the reading column on each side: the
 * whole side inset while [pageBackground] hides margins narrower than [MinSheetMargin], so the band
 * reaches the window's edges as it does on a window no wider than the column; zero otherwise, so
 * beside drawn margins it ends at the column's edges.
 */
val LocalReadingBandBleed = compositionLocalOf { 0.dp }

/**
 * Fills a full-width band, such as a section header, with [color], painted past its bounds by
 * [LocalReadingBandBleed] so it never stops a sliver short of the window's edges.
 */
@Composable
fun Modifier.readingBand(color: Color): Modifier {
    val bleed = LocalReadingBandBleed.current
    return drawBehind {
        val px = bleed.toPx()
        drawRect(color, topLeft = Offset(-px, 0f), size = Size(size.width + px * 2, size.height))
    }
}

/** [ReadingWidthBox] for a list with no padding of its own. */
@Composable
fun ReadingWidthBox(
    modifier: Modifier = Modifier,
    content: @Composable (PaddingValues) -> Unit,
) = ReadingWidthBox(PaddingValues(), modifier, content = content)

/**
 * Narrowest margin drawn beside the reading column. A thinner strip of [pageBackdrop] reads as a
 * rendering glitch rather than a margin, so [pageBackground] skips it.
 */
val MinSheetMargin = 24.dp

/**
 * Paints a screen's content area: [page] under the centered reading column and [backdrop] in the
 * margins beside it, full height, so on a wide window the column reads as a sheet and header bands
 * capped to it end at its edges. Until the margins would be [MinSheetMargin] wide, it is a plain
 * [narrow] fill, so a window a little wider than the column looks like one no wider.
 */
fun Modifier.pageBackground(
    page: Color,
    backdrop: Color,
    narrow: Color = page,
): Modifier =
    drawBehind {
        val inset = sheetInset(size.width, ReadingMaxWidth.toPx())
        if (!drawsSheetMargins(inset, MinSheetMargin.toPx())) {
            drawRect(narrow)
        } else {
            drawRect(backdrop)
            drawRect(
                page,
                topLeft = Offset(inset, 0f),
                size = Size(size.width - inset * 2, size.height),
            )
        }
    }

/** Width of each margin beside a centered [column] in [width], whole pixels; 0 or less if none. */
internal fun sheetInset(
    width: Float,
    column: Float,
): Float = kotlin.math.round((width - column) / 2f)

/** Whether margins [inset] wide are drawn: only once they are at least [minMargin] wide. */
internal fun drawsSheetMargins(
    inset: Float,
    minMargin: Float,
): Boolean = inset >= minMargin

/**
 * How far a band paints past a centered [column] in [width] on each side: the whole margin while
 * margins narrower than [minMargin] are hidden, else nothing. Whole pixels, like [sheetInset].
 */
internal fun bandBleed(
    width: Float,
    column: Float,
    minMargin: Float,
): Float {
    val inset = sheetInset(width, column)
    return if (inset > 0f && !drawsSheetMargins(inset, minMargin)) inset else 0f
}

private val ColorScheme.isDark: Boolean get() = background.luminance() < 0.5f

/**
 * The sheet under the reading column on a wide window: the lightest tone in light, and in dark the
 * elevated one, lighter than the backdrop, so the column sits above the margins rather than in a well.
 */
val ColorScheme.pageSheet: Color get() = if (isDark) surfaceContainer else background

/** The backdrop beside the reading column on a wide window, a step below [pageSheet]. */
val ColorScheme.pageBackdrop: Color get() = if (isDark) background else surfaceContainer

/**
 * [pageBackground] in the theme's colors: beside a rail, [pageSheet] on the column and
 * [pageBackdrop] beside it; background when there are no margins or no rail, so phones and narrow
 * windows are unchanged.
 */
@Composable
fun Modifier.pageBackground(): Modifier {
    val colors = MaterialTheme.colorScheme
    return if (LocalNavigationRailShown.current) {
        pageBackground(
            page = colors.pageSheet,
            backdrop = colors.pageBackdrop,
            narrow = colors.background,
        )
    } else {
        pageBackground(page = colors.background, backdrop = colors.background)
    }
}

/**
 * The fill under a centred message (loading, empty, error, sign-in): the plain page colour, as on a
 * phone. A sheet with only a short message on it reads as an empty column, so such states skip it.
 */
val ColorScheme.messageBackground: Color get() = background

/**
 * Paints [messageBackground] over the whole area, covering any reading-column sheet a screen's
 * container draws, so a centred message sits on one colour beside the rail.
 */
@Composable
fun Modifier.messageBackground(): Modifier = background(MaterialTheme.colorScheme.messageBackground)
