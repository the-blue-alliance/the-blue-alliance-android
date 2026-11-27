package com.thebluealliance.android.ui.components

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class EventDateRangeTest {
    @Test
    fun `range within a year joins its ends with an en dash`() {
        assertEquals(
            "Fri, Mar 20 – Sun, Mar 22, 2026",
            formatEventDateRange("2026-03-20", "2026-03-22"),
        )
    }

    @Test
    fun `range across years joins its ends with an en dash`() {
        assertEquals(
            "Thu, Dec 31, 2026 – Fri, Jan 1, 2027",
            formatEventDateRange("2026-12-31", "2027-01-01"),
        )
    }

    @Test
    fun `one-day event has no range`() {
        assertEquals("Fri, Mar 20, 2026", formatEventDateRange("2026-03-20", "2026-03-20"))
    }
}
