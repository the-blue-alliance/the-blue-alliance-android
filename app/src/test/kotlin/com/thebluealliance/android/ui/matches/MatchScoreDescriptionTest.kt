package com.thebluealliance.android.ui.matches

import com.thebluealliance.android.domain.BREAKDOWN_MISSING
import com.thebluealliance.android.domain.formatBreakdownValue
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
    fun `one point reads singular`() {
        assertEquals("1 point", scoreDescription(1))
    }

    @Test
    fun `zero and two points read plural`() {
        assertEquals("0 points", scoreDescription(0))
        assertEquals("2 points", scoreDescription(2))
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

    @Test
    fun `breakdown row names each value's alliance`() {
        assertEquals(
            "Auto Leave Points: Red 12; Blue 6",
            breakdownRowDescription("Auto Leave Points", "12", "6"),
        )
    }

    @Test
    fun `breakdown row reads a missing value as none`() {
        assertEquals(
            "Coral: Red none; Blue 3",
            breakdownRowDescription("Coral", BREAKDOWN_MISSING, "3"),
        )
    }

    @Test
    fun `breakdown row names playoff alliances with their colour`() {
        assertEquals(
            "Foul Points: Alliance 1, Red 0; Alliance 4, Blue 5",
            breakdownRowDescription("Foul Points", "0", "5", "Alliance 1", "Alliance 4"),
        )
    }

    @Test
    fun `breakdown row reads an RP bonus and a miss as words`() {
        assertEquals(
            "Auto Bonus: Red yes, plus 1 ranking point; Blue no",
            breakdownRowDescription(
                "Auto Bonus",
                formatBreakdownValue("autoBonusAchieved", "true"),
                formatBreakdownValue("autoBonusAchieved", "false"),
            ),
        )
    }

    @Test
    fun `breakdown row reads a plain boolean as yes or no`() {
        assertEquals(
            "Coopertition: Red yes; Blue no",
            breakdownRowDescription(
                "Coopertition",
                formatBreakdownValue("coopertitionCriteriaMet", "true"),
                formatBreakdownValue("coopertitionCriteriaMet", "false"),
            ),
        )
    }

    @Test
    fun `breakdown row spells out ranking points`() {
        assertEquals(
            "RP: Red plus 1 ranking point; Blue plus 3 ranking points",
            breakdownRowDescription(
                "RP",
                formatBreakdownValue("rp", "1"),
                formatBreakdownValue("rp", "3"),
            ),
        )
    }

    @Test
    fun `breakdown row reads deducted foul points as minus`() {
        assertEquals(
            "Foul Points: Red minus 6; Blue 0",
            breakdownRowDescription(
                "Foul Points",
                formatBreakdownValue("foul_points", "6"),
                formatBreakdownValue("foul_points", "0"),
            ),
        )
    }

    @Test
    fun `plain numbers read unchanged`() {
        assertEquals("42", spokenBreakdownValue(formatBreakdownValue("totalPoints", "42")))
    }

    @Test
    fun `breakdown row reads endgame enums as words`() {
        assertEquals(
            "Endgame 1: Red Deep cage; Blue None",
            breakdownRowDescription(
                "Endgame 1",
                formatBreakdownValue("endGameRobot1", "DeepCage"),
                formatBreakdownValue("endGameRobot1", "None"),
            ),
        )
    }

    @Test
    fun `team link names its colour alliance`() {
        assertEquals("Red alliance, team 254", allianceTeamDescription(null, "red", "254"))
        assertEquals("Blue alliance, team 1114", allianceTeamDescription(null, "blue", "1114"))
    }

    @Test
    fun `team link names its playoff alliance`() {
        assertEquals("Alliance 1, team 254", allianceTeamDescription("Alliance 1", "red", "254"))
    }
}
