package com.thebluealliance.android.wear.worker

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.Locale

class MatchClockTest {
    private val zone = ZoneId.of("America/Los_Angeles")
    private val now = ZonedDateTime.of(2027, 3, 19, 9, 0, 0, 0, zone) // a Friday
    private val todayAt1430 = ZonedDateTime.of(2027, 3, 19, 14, 30, 0, 0, zone).toEpochSecond()
    private val saturdayAt0915 = ZonedDateTime.of(2027, 3, 20, 9, 15, 0, 0, zone).toEpochSecond()

    @Test
    fun `today in 24-hour time`() {
        assertEquals(
            "14:30",
            MatchLogic.formatMatchClock(todayAt1430, now, use24Hour = true, Locale.US),
        )
    }

    @Test
    fun `today in 12-hour time`() {
        assertEquals(
            "2:30 PM",
            MatchLogic.formatMatchClock(todayAt1430, now, use24Hour = false, Locale.US),
        )
    }

    @Test
    fun `another day includes the weekday and the time`() {
        assertEquals(
            "Sat 9:15",
            MatchLogic.formatMatchClock(saturdayAt0915, now, use24Hour = true, Locale.US),
        )
        assertEquals(
            "Sat 9:15 AM",
            MatchLogic.formatMatchClock(saturdayAt0915, now, use24Hour = false, Locale.US),
        )
    }
}
