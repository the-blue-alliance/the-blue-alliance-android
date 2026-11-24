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
        assertEquals("Winner", allianceScoreState("red", "red"))
        assertNull(allianceScoreState("red", "blue"))
    }

    @Test
    fun `tie and unplayed mark neither alliance`() {
        assertNull(allianceScoreState("", "red"))
        assertNull(allianceScoreState(null, "blue"))
    }
}
