package com.thebluealliance.android.ui.regional

import com.thebluealliance.android.ui.common.ResourceQuantityStrings
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class RegionalRowDescriptionTest {
    private val strings = ResourceQuantityStrings

    @Test
    fun `a full row reads every column with its name`() {
        assertEquals(
            "Rank 1, Team 254, Event 1: 60 points, Event 2: 55 points, bonus 5, " +
                "120 total points, qualified: Regional Winner",
            regionalRowDescription(1, "254", 60, 55, 5, 120, "Regional Winner", strings),
        )
    }

    @Test
    fun `empty cells read as none`() {
        assertEquals(
            "Rank 30, Team 9999, Event 1: 12 points, Event 2 none, bonus none, " +
                "12 total points, not qualified",
            regionalRowDescription(30, "9999", 12, null, 0, 12, null, strings),
        )
    }

    @Test
    fun `one point reads singular`() {
        assertEquals(
            "Rank 40, Team 1, Event 1: 1 point, Event 2 none, bonus none, " +
                "1 total point, not qualified",
            regionalRowDescription(40, "1", 1, null, 0, 1, null, strings),
        )
    }
}
