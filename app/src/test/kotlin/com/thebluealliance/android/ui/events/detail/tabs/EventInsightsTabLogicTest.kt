package com.thebluealliance.android.ui.events.detail.tabs

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class EventInsightsTabLogicTest {
    @Test
    fun `opr row description labels each column`() {
        assertEquals(
            "Team 33, OPR 71.09, DPR 17.28, CCWM 53.81",
            oprRowDescription("33", 71.0912, 17.2777, 53.81),
        )
    }

    @Test
    fun `opr row description keeps sign and two decimals`() {
        assertEquals(
            "Team 9999, OPR 0.00, DPR 5.50, CCWM -5.50",
            oprRowDescription("9999", 0.0, 5.5, -5.5),
        )
    }

    @Test
    fun `copr row description names the selected stat`() {
        assertEquals(
            "Team 254, Total Auto Points 12.35",
            coprRowDescription("254", "Total Auto Points", 12.345678),
        )
    }
}
