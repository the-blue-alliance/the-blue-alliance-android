package com.thebluealliance.android.wear.worker

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class UpcomingEventRowTest {
    @Test
    fun `date and name are separated by a middle dot`() {
        assertEquals("Mar 20 · Hartford", upcomingEventRow("Mar 20", "Hartford"))
    }

    @Test
    fun `unknown date leaves just the name`() {
        assertEquals("Hartford", upcomingEventRow("", "Hartford"))
    }
}
