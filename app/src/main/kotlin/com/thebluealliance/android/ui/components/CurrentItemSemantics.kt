package com.thebluealliance.android.ui.components

import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics

/**
 * Marks the current entry of a picker menu as selected. Others get no selected state at all,
 * since `selected = false` makes every one of them announce "Not selected".
 */
internal fun Modifier.selectedIfCurrent(isCurrent: Boolean): Modifier =
    if (isCurrent) semantics { selected = true } else this
