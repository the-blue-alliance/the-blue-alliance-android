package com.thebluealliance.android.ui.components

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

/**
 * Centers the content and caps it at [ReadingMaxWidth], so lines stay readable on wide windows.
 * Windows narrower than the cap lay out exactly as without it. For scrolling lists use
 * [ReadingWidthBox] instead, so the whole width still scrolls.
 */
fun Modifier.readingWidth(): Modifier =
    fillMaxWidth()
        .wrapContentWidth(Alignment.CenterHorizontally)
        .widthIn(max = ReadingMaxWidth)

/** Side inset that centers a [ReadingMaxWidth] column in [availableWidth]; zero when narrower. */
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
        val inset = readingInset(maxWidth)
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
) = ReadingWidthBox(PaddingValues(), modifier, content)
