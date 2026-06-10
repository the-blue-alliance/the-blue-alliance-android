package com.thebluealliance.android.ui.components

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class MatchTimeFormatTest {
    // 2026-01-01T00:00:00Z
    private val newYearsUtc = 1767225600L

    @Test
    fun `formats in the event timezone when provided`() {
        assertEquals("Wed 7:00p", formatMatchTime(newYearsUtc, "America/New_York"))
        assertEquals("Thu 9:00a", formatMatchTime(newYearsUtc, "Asia/Tokyo"))
    }

    @Test
    fun `falls back to device timezone when the zone id is invalid or missing`() {
        val deviceLocal = formatMatchTime(newYearsUtc, null)
        assertEquals(deviceLocal, formatMatchTime(newYearsUtc, "Not/AZone"))
    }

    @Test
    fun `null time renders an em dash`() {
        assertEquals("—", formatMatchTime(null, "America/New_York"))
    }
}
