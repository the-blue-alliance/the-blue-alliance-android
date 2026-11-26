package com.thebluealliance.android.ui.components

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class FitColumnWidthsTest {
    private val weights = listOf(0.11f, 0.22f, 0.15f, 0.23f, 0.19f, 0.10f)

    @Test
    fun `weights that fit lay out unchanged`() {
        assertNull(fitColumnWidths(387f, weights, listOf(38f, 53f, 53f, 82f, 45f, 32f)))
    }

    @Test
    fun `a short column gets its minimum and the rest share the remainder`() {
        val mins = listOf(38f, 53f, 53f, 82f, 45f, 32f)
        val widths = fitColumnWidths(336f, weights, mins)!!
        assertEquals(336f, widths.sum(), 0.01f)
        widths.indices.forEach { assertTrue(widths[it] >= mins[it] - 0.01f, "column $it") }
        assertEquals(38f, widths[0], 0.01f)
        assertEquals(53f, widths[2], 0.01f)
    }

    @Test
    fun `columns pushed short by others are pinned too`() {
        val widths = fitColumnWidths(100f, listOf(1f, 1f, 1f), listOf(60f, 30f, 5f))!!
        assertEquals(listOf(60f, 30f, 10f), widths.map { Math.round(it).toFloat() })
    }

    @Test
    fun `minimums wider than the table are returned for scrolling`() {
        val mins = listOf(38f, 68f, 53f, 98f, 85f, 32f)
        assertEquals(mins, fitColumnWidths(256f, weights, mins))
    }
}
