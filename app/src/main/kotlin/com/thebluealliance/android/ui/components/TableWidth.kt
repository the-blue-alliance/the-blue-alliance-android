package com.thebluealliance.android.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Widest a data table grows; beyond it, weighted columns spread too far apart to scan. */
val TABLE_MAX_WIDTH = 720.dp

/**
 * Fills the available width up to [TABLE_MAX_WIDTH], centered. Apply it to a table's header and
 * to each of its rows so they share one column grid; it is a no-op on narrower windows.
 */
fun Modifier.tableWidth(): Modifier =
    this
        .fillMaxWidth()
        .wrapContentWidth(Alignment.CenterHorizontally)
        .widthIn(max = TABLE_MAX_WIDTH)
        .fillMaxWidth()

/** Side inset that centers a [TABLE_MAX_WIDTH] column in [availableWidth]; zero when narrower. */
internal fun tableInset(availableWidth: Dp): Dp =
    ((availableWidth - TABLE_MAX_WIDTH) / 2).coerceAtLeast(0.dp)
