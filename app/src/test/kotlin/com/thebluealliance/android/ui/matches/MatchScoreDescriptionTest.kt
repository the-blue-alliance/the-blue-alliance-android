package com.thebluealliance.android.ui.matches

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class MatchScoreDescriptionTest {
    @Test
    fun `played score reads as points`() {
        assertEquals("251 points", scoreDescription(251))
        assertEquals("0 points", scoreDescription(0))
    }

    @Test
    fun `unplayed score reads as not played`() {
        assertEquals("Not played", scoreDescription(-1))
    }

    @Test
    fun `only the winning alliance is marked`() {
        assertEquals("Winner", allianceScoreState("red", "red", 120))
        assertNull(allianceScoreState("red", "blue", 98))
    }

    @Test
    fun `played tie marks both alliances`() {
        assertEquals("Tie", allianceScoreState("", "red", 100))
        assertEquals("Tie", allianceScoreState("", "blue", 100))
        assertEquals("Tie", allianceScoreState("", "red", 0))
    }

    @Test
    fun `unplayed marks neither alliance`() {
        assertNull(allianceScoreState("", "red", -1))
        assertNull(allianceScoreState(null, "blue", -1))
    }

    @Test
    fun `missing winner on a played match is not a tie`() {
        assertNull(allianceScoreState(null, "red", 100))
    }

    @Test
    fun `playoff alliance label also names the colour`() {
        assertEquals("Alliance 1, Red", allianceLabelDescription("Alliance 1", "red"))
        assertEquals("Alliance 8, Blue", allianceLabelDescription("Alliance 8", "blue"))
    }

    @Test
    fun `colour label needs no description`() {
        assertNull(allianceLabelDescription(null, "red"))
    }
}
