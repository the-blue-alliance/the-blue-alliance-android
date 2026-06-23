package com.thebluealliance.android.domain

import com.thebluealliance.android.domain.model.CompLevel
import com.thebluealliance.android.domain.model.Match
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class MatchScoreBreakdownTest {
    private fun match(
        eventKey: String = "2024bcvi",
        compLevel: CompLevel = CompLevel.QUAL,
        redScore: Int = 80,
        scoreBreakdown: String?,
    ) = Match(
        key = "${eventKey}_qm1",
        eventKey = eventKey,
        compLevel = compLevel,
        matchNumber = 1,
        setNumber = 1,
        time = null,
        predictedTime = null,
        actualTime = null,
        redTeamKeys = listOf("frc1", "frc2", "frc3"),
        redScore = redScore,
        blueTeamKeys = listOf("frc4", "frc5", "frc6"),
        blueScore = 60,
        winningAlliance = "red",
        scoreBreakdown = scoreBreakdown,
    )

    @Test
    fun `2024 has exactly two rp bonuses and coopertition is not one of them`() {
        assertEquals(
            listOf("melodyBonusAchieved", "ensembleBonusAchieved"),
            rpBonusFieldsByYear[2024],
        )
    }

    @Test
    fun `2024 coopertition does not produce an rp dot but melody and ensemble do`() {
        val breakdown =
            """
            {
              "red": {"coopertitionBonusAchieved": true, "melodyBonusAchieved": true, "ensembleBonusAchieved": false},
              "blue": {"coopertitionBonusAchieved": true, "melodyBonusAchieved": false, "ensembleBonusAchieved": false}
            }
            """.trimIndent()

        val bonuses = match(scoreBreakdown = breakdown).rpBonuses()

        assertEquals(listOf(true, false), bonuses?.red)
        assertEquals(listOf(false, false), bonuses?.blue)
    }

    @Test
    fun `unplayed and non-qual matches have no rp bonuses`() {
        val breakdown = """{"red": {"melodyBonusAchieved": true}, "blue": {}}"""

        assertNull(match(redScore = -1, scoreBreakdown = breakdown).rpBonuses())
        assertNull(match(compLevel = CompLevel.SEMIFINAL, scoreBreakdown = breakdown).rpBonuses())
        assertNull(match(scoreBreakdown = null).rpBonuses())
    }

    @Test
    fun `malformed breakdown json returns null instead of throwing`() {
        assertNull(match(scoreBreakdown = "not json {").rpBonuses())
    }

    @Test
    fun `unsupported year returns null`() {
        val breakdown = """{"red": {}, "blue": {}}"""

        assertNull(match(eventKey = "2019abc", scoreBreakdown = breakdown).rpBonuses())
    }

    @Test
    fun `2024 coopertition formats as plain check without rp credit`() {
        assertEquals("✓", formatBreakdownValue("coopertitionBonusAchieved", "true"))
        assertEquals("✗", formatBreakdownValue("coopertitionBonusAchieved", "false"))
    }

    @Test
    fun `rp bonus fields format with rp credit`() {
        assertEquals("✓ (+1 RP)", formatBreakdownValue("melodyBonusAchieved", "true"))
        assertEquals("+2 RP", formatBreakdownValue("rp", "2"))
        assertEquals("42", formatBreakdownValue("totalPoints", "42"))
    }
}
