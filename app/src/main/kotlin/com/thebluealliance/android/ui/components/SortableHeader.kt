package com.thebluealliance.android.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.thebluealliance.android.ui.theme.OnBrandRipple

/** Spoken sort state of a table header cell; the visual carat carries no description of its own. */
internal fun sortStateDescription(
    sorted: Boolean,
    ascending: Boolean,
): String =
    when {
        !sorted -> "Not sorted"
        ascending -> "Sorted ascending"
        else -> "Sorted descending"
    }

/** Room between a table cell's label and the edge of its highlight on the label's side. */
val TableCellHighlightPadding: Dp = 8.dp

/** How far a [tableCellTarget] highlight reaches past the cell's start and end edges. */
internal data class TableCellBleed(
    val start: Dp,
    val end: Dp,
)

/**
 * Bleed for a cell whose label sits [gutter] in from its start edge, or its end edge when
 * [labelAtEnd]: whatever [gutter] is short of [TableCellHighlightPadding], on the label's side
 * only. The highlight pads its content by the bleed and then the gutter, so the label gets
 * [TableCellHighlightPadding] of room while staying [gutter] from the cell's edge, in line with the
 * column below.
 */
internal fun tableCellBleed(
    gutter: Dp,
    labelAtEnd: Boolean,
): TableCellBleed {
    val bleed = (TableCellHighlightPadding - gutter).coerceAtLeast(0.dp)
    return if (labelAtEnd) TableCellBleed(0.dp, bleed) else TableCellBleed(bleed, 0.dp)
}

/**
 * Makes a table cell a target whose rounded highlight ([shape]) keeps [TableCellHighlightPadding]
 * between its edge and the label, without moving the label (see [tableCellBleed]). [interaction]
 * is the clickable that draws the highlight. Chain sizing modifiers before this one and any
 * vertical padding after it.
 */
fun Modifier.tableCellTarget(
    shape: Shape,
    gutter: Dp,
    labelAtEnd: Boolean,
    interaction: Modifier,
): Modifier {
    val bleed = tableCellBleed(gutter, labelAtEnd)
    // The bleed and the gutter are separate paddings so each rounds to whole pixels exactly as
    // the bleed's offset does, which keeps the label on the same pixel as without the bleed.
    return this
        .bleedHorizontally(start = bleed.start, end = bleed.end)
        .clip(shape)
        .then(interaction)
        .padding(start = bleed.start, end = bleed.end)
        .padding(horizontal = gutter)
}

/**
 * Makes a table header cell a sort button announcing [label] and its sort state, drawn as a
 * [tableCellTarget]. Sort headers sit on the brand header blue, so the highlight is [OnBrandRipple]. Chain sizing modifiers before it and the cell's vertical padding after it.
 */
fun Modifier.sortableHeader(
    shape: Shape,
    label: String,
    sorted: Boolean,
    ascending: Boolean,
    gutter: Dp,
    labelAtEnd: Boolean,
    onClick: () -> Unit,
): Modifier =
    this
        .semantics { stateDescription = sortStateDescription(sorted, ascending) }
        .tableCellTarget(
            shape = shape,
            gutter = gutter,
            labelAtEnd = labelAtEnd,
            interaction =
                Modifier.handCursor().clickable(
                    interactionSource = null,
                    indication = OnBrandRipple,
                    onClickLabel = "Sort by $label",
                    role = Role.Button,
                    onClick = onClick,
                ),
        )
