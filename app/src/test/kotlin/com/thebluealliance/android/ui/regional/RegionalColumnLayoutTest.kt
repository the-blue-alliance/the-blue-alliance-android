package com.thebluealliance.android.ui.regional

import androidx.compose.ui.unit.dp
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class RegionalColumnLayoutTest {
    @Test
    fun `columns that fit keep their weights`() {
        val layout = regionalColumnLayout(411.dp, List(7) { 20.dp })
        assertNull(layout.columnWidths)
        assertNull(layout.rowWidth)
    }

    @Test
    fun `a column too narrow for its value widens without scrolling when the rest have room`() {
        val minWidths = listOf(20.dp, 90.dp, 20.dp, 20.dp, 20.dp, 20.dp, 20.dp)
        val layout = regionalColumnLayout(411.dp, minWidths)
        assertEquals(98.dp, layout.columnWidths!![1])
        assertNull(layout.rowWidth)
    }

    @Test
    fun `values that just fit their weights keep the weights without gutters`() {
        // 379dp of columns; the Rank weight gives it about 38.5dp.
        val layout = regionalColumnLayout(411.dp, listOf(38.dp) + List(6) { 20.dp })
        assertNull(layout.columnWidths)
    }

    @Test
    fun `columns wider than the window scroll with a gutter between values`() {
        val layout = regionalColumnLayout(411.dp, List(7) { 80.dp })
        assertEquals(List(6) { 88.dp } + 80.dp, layout.columnWidths)
        assertEquals(88.dp * 6 + 80.dp + 32.dp, layout.rowWidth)
    }
}
