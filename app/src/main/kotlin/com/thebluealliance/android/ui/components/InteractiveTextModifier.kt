package com.thebluealliance.android.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.toggleable
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
 * element's layout bounds (see [bleedHorizontally]) instead of pushing the content in, so the
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
    padded(
        shape,
        horizontal,
        vertical,
        keepContentInPlace,
        Modifier.clickable(onClickLabel = onClickLabel, role = role, onClick = onClick),
    )

/**
 * The [paddedClickable] chain for a two-state control: [value] toggles via [onValueChange], with
 * the same inner padding, [shape] clip and optional [keepContentInPlace] outset.
 *
 * [role] defaults to [Role.Switch]; pass [Role.Checkbox] for a checkbox row.
 */
fun Modifier.paddedToggleable(
    value: Boolean,
    shape: Shape,
    horizontal: Dp = PaddedClickableHorizontalPadding,
    vertical: Dp = 0.dp,
    keepContentInPlace: Boolean = false,
    role: Role? = Role.Switch,
    onValueChange: (Boolean) -> Unit,
): Modifier =
    padded(
        shape,
        horizontal,
        vertical,
        keepContentInPlace,
        Modifier.toggleable(value = value, role = role, onValueChange = onValueChange),
    )

/** Outset (optional), clip, hand cursor, [interaction], then the inner padding, in the one order that works. */
private fun Modifier.padded(
    shape: Shape,
    horizontal: Dp,
    vertical: Dp,
    keepContentInPlace: Boolean,
    interaction: Modifier,
): Modifier =
    this
        .then(
            if (keepContentInPlace) {
                Modifier.bleedHorizontally(
                    start = horizontal,
                    end = horizontal,
                )
            } else {
                Modifier
            },
        ).clip(shape)
        .returnsFocus()
        .handCursor()
        .then(interaction)
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
 * Lets the rest of the chain extend [start] and [end] past this element's edges without changing
 * the space it takes in its parent.
 */
internal fun Modifier.bleedHorizontally(
    start: Dp,
    end: Dp,
): Modifier =
    layout { measurable, constraints ->
        val startPx = start.roundToPx()
        val totalPx = startPx + end.roundToPx()
        val placeable = measurable.measure(bleedConstraints(constraints, totalPx))
        layout(bleedLayoutWidth(constraints, placeable.width, totalPx), placeable.height) {
            placeable.placeRelative(-startPx, 0)
        }
    }

/** Constraints for the bled content: [totalPx] wider than the element's own. */
internal fun bleedConstraints(
    constraints: Constraints,
    totalPx: Int,
): Constraints = constraints.offset(horizontal = totalPx)

/** The width the bled element reports to its parent: the content's, less [totalPx]. */
internal fun bleedLayoutWidth(
    constraints: Constraints,
    contentWidth: Int,
    totalPx: Int,
): Int = constraints.constrainWidth(contentWidth - totalPx)
