package com.thebluealliance.android.ui.components

import androidx.compose.ui.unit.dp
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class TableWidthTest {
    @Test
    fun `phones fill the window`() {
        assertEquals(TableSpan(0.dp, 412.dp), tableSpan(412.dp))
    }

    @Test
    fun `a table narrower than the column keeps its own width at the column start`() {
        // 1180dp panel: the 840dp column starts 170dp in.
        assertEquals(TableSpan(170.dp, 720.dp), tableSpan(1180.dp))
        assertEquals(TableSpan(170.dp, 512.dp), tableSpan(1180.dp, 512.dp))
    }

    @Test
    fun `a table wider than the column is capped at the column`() {
        assertEquals(TableSpan(170.dp, 840.dp), tableSpan(1180.dp, 1000.dp))
    }

    @Test
    fun `between the table and column widths the table starts at the edge`() {
        assertEquals(TableSpan(0.dp, 720.dp), tableSpan(800.dp))
        assertEquals(TableSpan(0.dp, 800.dp), tableSpan(800.dp, 1000.dp))
    }

    @Test
    fun `rows that fit don't scroll`() {
        assertNull(sideScrollRowWidth(360f, null, 12f, frozenCount = 0))
        assertNull(sideScrollRowWidth(360f, listOf(100f, 236f), 12f, frozenCount = 0))
        assertNull(sideScrollRowWidth(360f, listOf(40f, 60f, 100f, 136f), 12f, frozenCount = 2))
    }

    @Test
    fun `overflowing rows scroll their columns and end padding`() {
        assertEquals(372f, sideScrollRowWidth(360f, listOf(100f, 260f), 12f, frozenCount = 0))
    }

    @Test
    fun `frozen columns stay out of the scrolling width`() {
        assertEquals(
            272f,
            sideScrollRowWidth(360f, listOf(40f, 60f, 100f, 160f), 12f, frozenCount = 2),
        )
    }
}
