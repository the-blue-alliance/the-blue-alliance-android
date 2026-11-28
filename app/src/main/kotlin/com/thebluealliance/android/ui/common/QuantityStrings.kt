package com.thebluealliance.android.ui.common

import android.content.res.Resources
import androidx.annotation.PluralsRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalResources

/** Picks a `<plurals>` item for [count] and formats [count] into it, so "1 point" stays singular. */
fun interface QuantityStrings {
    fun get(
        @PluralsRes id: Int,
        count: Int,
    ): String
}

fun Resources.quantityStrings(): QuantityStrings =
    QuantityStrings { id, count -> getQuantityString(id, count, count) }

/** [QuantityStrings] for the current resources, for count text built outside a composable call. */
@Composable
fun rememberQuantityStrings(): QuantityStrings {
    val resources = LocalResources.current
    // Resources can update in place, so a configuration change must still re-key callers.
    return remember(resources, LocalConfiguration.current) { resources.quantityStrings() }
}
