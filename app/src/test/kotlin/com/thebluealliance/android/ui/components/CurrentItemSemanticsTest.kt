package com.thebluealliance.android.ui.components

import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.SemanticsModifier
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class CurrentItemSemanticsTest {
    @Suppress("DEPRECATION")
    private fun Modifier.selectedState(): Boolean? =
        foldIn<Boolean?>(null) { acc, element ->
            (element as? SemanticsModifier)
                ?.semanticsConfiguration
                ?.getOrNull(SemanticsProperties.Selected) ?: acc
        }

    @Test
    fun `current entry is selected`() {
        assertEquals(true, Modifier.selectedIfCurrent(true).selectedState())
    }

    @Test
    fun `other entries carry no selected state`() {
        assertNull(Modifier.selectedIfCurrent(false).selectedState())
    }
}
