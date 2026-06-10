package com.thebluealliance.android.domain

import com.thebluealliance.android.domain.model.CompLevel
import com.thebluealliance.android.domain.model.Match
import com.thebluealliance.android.domain.model.PlayoffType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class MatchAdvancementMsgsTest {
    private fun match(
        setNumber: Int,
        compLevel: CompLevel = CompLevel.SEMIFINAL,
        winningAlliance: String? = "red",
        redScore: Int = 100,
        blueScore: Int = 80,
    ) = Match(
        key = "2026test_sf${setNumber}m1",
        eventKey = "2026test",
        compLevel = compLevel,
        matchNumber = 1,
        setNumber = setNumber,
        time = null,
        predictedTime = null,
        actualTime = null,
        redTeamKeys = listOf("frc1", "frc2", "frc3"),
        redScore = redScore,
        blueTeamKeys = listOf("frc4", "frc5", "frc6"),
        blueScore = blueScore,
        winningAlliance = winningAlliance,
    )

    // FIRST Game Manual Table 11-3 (2023+ double elim):
    // M11 = upper final (winner -> Finals, loser -> M13); M12 = lower R4
    // (winner -> M13, loser out); M13 = lower final (winner -> Finals).
    @Test
    fun `eight team bracket routes every set per the official table`() {
        val expected =
            mapOf(
                1 to ("Advances to Upper Bracket - R2-7" to "Advances to Lower Bracket - R2-5"),
                2 to ("Advances to Upper Bracket - R2-7" to "Advances to Lower Bracket - R2-5"),
                3 to ("Advances to Upper Bracket - R2-8" to "Advances to Lower Bracket - R2-6"),
                4 to ("Advances to Upper Bracket - R2-8" to "Advances to Lower Bracket - R2-6"),
                5 to ("Advances to Lower Bracket - R3-10" to "Eliminated"),
                6 to ("Advances to Lower Bracket - R3-9" to "Eliminated"),
                7 to ("Advances to Upper Bracket - R4-11" to "Advances to Lower Bracket - R3-9"),
                8 to ("Advances to Upper Bracket - R4-11" to "Advances to Lower Bracket - R3-10"),
                9 to ("Advances to Lower Bracket - R4-12" to "Eliminated"),
                10 to ("Advances to Lower Bracket - R4-12" to "Eliminated"),
                11 to ("Advances to Finals" to "Advances to Lower Bracket - R5-13"),
                12 to ("Advances to Lower Bracket - R5-13" to "Eliminated"),
                13 to ("Advances to Finals" to "Eliminated"),
            )

        expected.forEach { (set, winLose) ->
            val msgs = match(setNumber = set).getAdvancement(PlayoffType.DOUBLE_ELIM_8_TEAM)
            assertEquals(winLose.first, msgs?.red, "set $set red (winner)")
            assertEquals(winLose.second, msgs?.blue, "set $set blue (loser)")
        }
    }

    @Test
    fun `upper final loser is not eliminated and lower r4 winner goes to m13`() {
        val upperFinal = match(setNumber = 11, winningAlliance = "blue")
        val msgs = upperFinal.getAdvancement(PlayoffType.DOUBLE_ELIM_8_TEAM)

        assertEquals("Advances to Finals", msgs?.blue)
        assertEquals("Advances to Lower Bracket - R5-13", msgs?.red)
    }

    @Test
    fun `four team bracket unchanged`() {
        val msgs = match(setNumber = 3).getAdvancement(PlayoffType.DOUBLE_ELIM_4_TEAM)

        assertEquals("Advances to Finals", msgs?.red)
        assertEquals("Advances to Lower Bracket - R3-5", msgs?.blue)
    }

    @Test
    fun `unplayed match returns null instead of marking both alliances eliminated`() {
        val unplayed = match(setNumber = 11, winningAlliance = null, redScore = -1, blueScore = -1)

        assertNull(unplayed.getAdvancement(PlayoffType.DOUBLE_ELIM_8_TEAM))
    }

    @Test
    fun `tie with empty winning alliance returns null`() {
        val tie = match(setNumber = 9, winningAlliance = "", redScore = 50, blueScore = 50)

        assertNull(tie.getAdvancement(PlayoffType.DOUBLE_ELIM_8_TEAM))
    }

    @Test
    fun `finals and quals return null`() {
        assertNull(
            match(
                setNumber = 1,
                compLevel = CompLevel.FINAL,
            ).getAdvancement(PlayoffType.DOUBLE_ELIM_8_TEAM),
        )
        assertNull(
            match(
                setNumber = 1,
                compLevel = CompLevel.QUAL,
            ).getAdvancement(PlayoffType.DOUBLE_ELIM_8_TEAM),
        )
    }
}
