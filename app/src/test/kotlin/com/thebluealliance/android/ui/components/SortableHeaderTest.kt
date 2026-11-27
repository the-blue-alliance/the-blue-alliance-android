package com.thebluealliance.android.ui.components

import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class SortableHeaderTest {
    @Test
    fun `unsorted column says so regardless of direction`() {
        assertEquals("Not sorted", sortStateDescription(sorted = false, ascending = true))
        assertEquals("Not sorted", sortStateDescription(sorted = false, ascending = false))
    }

    @Test
    fun `sorted column names its direction`() {
        assertEquals("Sorted ascending", sortStateDescription(sorted = true, ascending = true))
        assertEquals("Sorted descending", sortStateDescription(sorted = true, ascending = false))
    }

    @Test
    fun `start label bleeds at the start up to 8dp of room`() {
        assertEquals(TableCellBleed(4.dp, 0.dp), tableCellBleed(gutter = 4.dp, labelAtEnd = false))
        assertEquals(TableCellHighlightPadding, 4.dp + tableCellBleed(4.dp, false).start)
    }

    @Test
    fun `end label bleeds at the end instead`() {
        assertEquals(TableCellBleed(0.dp, 4.dp), tableCellBleed(gutter = 4.dp, labelAtEnd = true))
    }

    @Test
    fun `a gutter already 8dp wide needs no bleed`() {
        assertEquals(TableCellBleed(0.dp, 0.dp), tableCellBleed(12.dp, false))
    }

    @Test
    fun `bleed widens the content but not the reported width`() {
        val constraints = Constraints.fixedWidth(100)
        val widened = bleedConstraints(constraints, totalPx = 8)
        assertEquals(108, widened.minWidth)
        assertEquals(108, widened.maxWidth)
        assertEquals(100, bleedLayoutWidth(constraints, contentWidth = 108, totalPx = 8))
    }
}
