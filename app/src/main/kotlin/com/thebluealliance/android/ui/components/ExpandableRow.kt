package com.thebluealliance.android.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription

/**
 * Makes a row toggle its expanded details: an Expanded/Collapsed state plus a click label that
 * names the action. Any chevron inside should have a null contentDescription.
 */
fun Modifier.expandableRow(
    expanded: Boolean,
    onToggle: () -> Unit,
): Modifier =
    this
        .semantics { stateDescription = if (expanded) "Expanded" else "Collapsed" }
        .clickable(
            onClickLabel = if (expanded) "Collapse details" else "Expand details",
            role = Role.Button,
            onClick = onToggle,
        )
