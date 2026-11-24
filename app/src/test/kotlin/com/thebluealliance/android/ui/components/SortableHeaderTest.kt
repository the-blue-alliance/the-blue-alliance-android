package com.thebluealliance.android.ui.components

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
}
