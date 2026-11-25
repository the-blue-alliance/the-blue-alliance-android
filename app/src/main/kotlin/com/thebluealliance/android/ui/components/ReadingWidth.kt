package com.thebluealliance.android.ui.components

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.layout.layout
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

/** [ReadingWidthBox] for a list with no padding of its own. */
@Composable
fun ReadingWidthBox(
    modifier: Modifier = Modifier,
    content: @Composable (PaddingValues) -> Unit,
) = ReadingWidthBox(PaddingValues(), modifier, content = content)

/**
 * Paints a screen's content area: [page] under the centered reading column and [backdrop] in the
 * margins beside it, full height, so on a wide window the column reads as a sheet and header bands
 * capped to it end at its edges. No wider than the column, it is a plain [narrow] fill.
 */
fun Modifier.pageBackground(
    page: Color,
    backdrop: Color,
    narrow: Color = page,
): Modifier =
    drawBehind {
        val inset = sheetInset(size.width, ReadingMaxWidth.toPx())
        if (inset <= 0f) {
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
