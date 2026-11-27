package com.thebluealliance.android.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.layout
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.constrainWidth
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.offset

/** Default side padding inside a [paddedClickable] highlight, so it never hugs the content. */
val PaddedClickableHorizontalPadding: Dp = 8.dp

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
 * [horizontal] defaults to [PaddedClickableHorizontalPadding] so the highlight's edge never runs
 * along the glyphs; a call site that really wants none must pass `0.dp`.
 *
 * Note that the padding is applied last, so it insets this element's *content* — chain any sizing
 * modifiers (`weight`, `fillMaxWidth`, ...) before this one, exactly as with the raw chain.
 *
 * Pick [shape] by the highlight-shape rules (AGENTS.md, "UI conventions"). A highlight that spans
 * its container edge to edge stays square; use a plain `clickable` for those. A highlight inset
 * from its container's edges, which is every use of this helper, is rounded to suit the element:
 * - one line of text or a number (links, score cells, footer links): `MaterialTheme.shapes.small`
 * - a card-sized, multi-line block (an inset match row, a media tile): `MaterialTheme.shapes.medium`
 *
 * With [keepContentInPlace], the horizontal padding grows the highlight outward past this
 * element's layout bounds (see [horizontalOutset]) instead of pushing the content in, so the
 * content stays aligned with its neighbours. Leave room for the bleed beside the element.
 *
 * [role] and [onClickLabel] are forwarded to `clickable` for accessibility services, e.g.
 * `onClickLabel = "View team 254"`.
 *
 * This convenience form covers symmetric horizontal/vertical padding, which every current call
 * site uses. If a per-edge (start/top/end/bottom) site appears, add a `PaddingValues` overload.
 */
fun Modifier.paddedClickable(
    shape: Shape,
    horizontal: Dp = PaddedClickableHorizontalPadding,
    vertical: Dp = 0.dp,
    keepContentInPlace: Boolean = false,
    role: Role? = Role.Button,
    onClickLabel: String? = null,
    onClick: () -> Unit,
): Modifier =
    this
        .then(if (keepContentInPlace) Modifier.horizontalOutset(horizontal) else Modifier)
        .clip(shape)
        .returnsFocus()
        .handCursor()
        .clickable(onClickLabel = onClickLabel, role = role, onClick = onClick)
        .padding(horizontal = horizontal, vertical = vertical)

/**
 * Minimum height of a link's drawn highlight: the 48dp Material target. Compose widens small
 * targets to 48dp only for touch, so a mouse gets exactly the drawn size.
 */
val MinLinkHeight = 48.dp

/**
 * How far a full-width link row's highlight sits in from each column edge. The label takes the
 * same inset inside the highlight, so it keeps the 16dp keyline.
 */
val LinkRowInset = 8.dp

/** Vertical padding that lifts a one-line `bodySmall` text link to [MinLinkHeight]. */
val SmallTextLinkVerticalPadding = 16.dp

/**
 * Lets the rest of the chain extend [outset] past this element's start and end edges without
 * changing the space it takes in its parent. Pair it with an equal inner padding to give a
 * highlight room around its content while the content keeps its position.
 */
fun Modifier.horizontalOutset(outset: Dp): Modifier =
    layout { measurable, constraints ->
        val outsetPx = outset.roundToPx()
        val placeable = measurable.measure(outsetConstraints(constraints, outsetPx))
        layout(outsetLayoutWidth(constraints, placeable.width, outsetPx), placeable.height) {
            placeable.place(-outsetPx, 0)
        }
    }

/** The constraints for the outset content: [outsetPx] wider on each side. */
internal fun outsetConstraints(
    constraints: Constraints,
    outsetPx: Int,
): Constraints = constraints.offset(horizontal = 2 * outsetPx)

/** The width the outset element reports to its parent: the content's, less both outsets. */
internal fun outsetLayoutWidth(
    constraints: Constraints,
    contentWidth: Int,
    outsetPx: Int,
): Int = constraints.constrainWidth(contentWidth - 2 * outsetPx)
