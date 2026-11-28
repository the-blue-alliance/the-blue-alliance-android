package com.thebluealliance.android.ui.regional

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class RegionalRowDescriptionTest {
    @Test
    fun `a full row reads every column with its name`() {
        assertEquals(
            "Rank 1, Team 254, Event 1: 60 points, Event 2: 55 points, bonus 5, " +
                "120 total points, qualified: Regional Winner",
            regionalRowDescription(1, "254", 60, 55, 5, 120, "Regional Winner"),
        )
    }

    @Test
    fun `empty cells read as none`() {
        assertEquals(
            "Rank 30, Team 9999, Event 1: 12 points, Event 2 none, bonus none, " +
                "12 total points, not qualified",
            regionalRowDescription(30, "9999", 12, null, 0, 12, null),
        )
    }
}
