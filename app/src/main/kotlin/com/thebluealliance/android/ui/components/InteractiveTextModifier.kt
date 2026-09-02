package com.thebluealliance.android.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Makes this element clickable with symmetric [horizontal]/[vertical] padding *inside* the tap
 * target, clipped to [shape].
 *
 * Prefer this over hand-rolling `clip` + `clickable` + `padding`: the order of those three
 * modifiers is easy to get wrong, and both wrong orders produce the same class of bug.
 *
 * - `padding(...).clickable(...)` puts the padding **outside** the tap target, so the ripple and
 *   pointer-hover highlight hug the text glyphs instead of filling the padded area, and the
 *   touch target is smaller than it looks.
 * - `clickable(...).padding(...)` without a preceding `clip(...)` draws a hard-cornered highlight
 *   that ignores the theme's rounded corners.
 *
 * Only `clip(shape).clickable(onClick).padding(...)` gives a highlight that both fills the padded
 * target and follows [shape]. Composing them here means call sites cannot get it wrong.
 *
 * Note that the padding is applied last, so it insets this element's *content* — chain any sizing
 * modifiers (`weight`, `fillMaxWidth`, ...) before this one, exactly as with the raw chain.
 *
 * This convenience form covers symmetric horizontal/vertical padding, which every current call
 * site uses. If a per-edge (start/top/end/bottom) site appears, add a `PaddingValues` overload.
 */
fun Modifier.paddedClickable(
    shape: Shape,
    horizontal: Dp = 0.dp,
    vertical: Dp = 0.dp,
    onClick: () -> Unit,
): Modifier =
    this
        .clip(shape)
        .clickable(onClick = onClick)
        .padding(horizontal = horizontal, vertical = vertical)
