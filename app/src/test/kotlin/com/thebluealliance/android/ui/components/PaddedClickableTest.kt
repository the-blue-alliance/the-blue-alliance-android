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
    fun `outset content gets both outsets of extra width`() {
        val constraints = outsetConstraints(Constraints(minWidth = 100, maxWidth = 300), 16)
        assertEquals(132, constraints.minWidth)
        assertEquals(332, constraints.maxWidth)
    }

    @Test
    fun `outset leaves unbounded width unbounded`() {
        assertEquals(Constraints.Infinity, outsetConstraints(Constraints(), 16).maxWidth)
    }

    @Test
    fun `outset element takes the same width as its content without the outset`() {
        assertEquals(50, outsetLayoutWidth(Constraints(maxWidth = 300), 82, 16))
        assertEquals(300, outsetLayoutWidth(Constraints(minWidth = 300, maxWidth = 300), 332, 16))
    }
}
