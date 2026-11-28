package com.thebluealliance.android.ui.components

import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class PaddedClickableTest {
    @Test
    fun `default side padding keeps the highlight off the content`() {
        assertEquals(8.dp, PaddedClickableHorizontalPadding)
    }

    @Test
    fun `bleed widens the content but not the reported width`() {
        val constraints = Constraints.fixedWidth(100)
        val widened = bleedConstraints(constraints, totalPx = 8)
        assertEquals(108, widened.minWidth)
        assertEquals(108, widened.maxWidth)
        assertEquals(100, bleedLayoutWidth(constraints, contentWidth = 108, totalPx = 8))
    }

    @Test
    fun `bleed leaves unbounded width unbounded`() {
        assertEquals(Constraints.Infinity, bleedConstraints(Constraints(), totalPx = 32).maxWidth)
    }
}
