package com.thebluealliance.android.widget

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class WidgetTeamLabelTest {
    @Test
    fun `nickname follows the number after a middle dot`() {
        assertEquals("254 · The Cheesy Poofs", widgetTeamLabel("254", "The Cheesy Poofs"))
    }

    @Test
    fun `blank nickname falls back to Team and the number`() {
        assertEquals("Team 254", widgetTeamLabel("254", ""))
        assertEquals("Team 254", widgetTeamLabel("254", "   "))
    }

    @Test
    fun `blank team number shows the widget name`() {
        assertEquals("Team Tracker", widgetTeamLabel("", ""))
        assertEquals("Team Tracker", widgetTeamLabel("  ", "The Cheesy Poofs"))
    }
}
