package com.thebluealliance.android.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription

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

/**
 * Makes a table header cell a sort button clipped to [shape], announcing [label] and its sort
 * state. Chain sizing modifiers before it and the cell's inner padding after it.
 */
fun Modifier.sortableHeader(
    shape: Shape,
    label: String,
    sorted: Boolean,
    ascending: Boolean,
    onClick: () -> Unit,
): Modifier =
    this
        .semantics { stateDescription = sortStateDescription(sorted, ascending) }
        .clip(shape)
        .clickable(onClickLabel = "Sort by $label", role = Role.Button, onClick = onClick)
