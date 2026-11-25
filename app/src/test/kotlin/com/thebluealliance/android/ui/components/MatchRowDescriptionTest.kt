package com.thebluealliance.android.ui.components

import com.thebluealliance.android.domain.AllianceRpBonuses
import com.thebluealliance.android.domain.model.Alliance
import com.thebluealliance.android.domain.model.CompLevel
import com.thebluealliance.android.domain.model.Match
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class MatchRowDescriptionTest {
    private fun match(
        redScore: Int = 251,
        blueScore: Int = 180,
        winningAlliance: String? = "red",
        redSurrogates: List<String> = emptyList(),
        redAlliance: Alliance? = null,
    ) = Match(
        key = "2026test_qm1",
        eventKey = "2026test",
        compLevel = CompLevel.QUAL,
        matchNumber = 1,
        setNumber = 1,
        time = null,
        predictedTime = null,
        actualTime = null,
        redTeamKeys = listOf("frc6324", "frc4907", "frc9496"),
        redSurrogateTeamKeys = redSurrogates,
        redScore = redScore,
        redPlayoffAlliance = redAlliance,
        blueTeamKeys = listOf("frc254", "frc1678", "frc118"),
        blueScore = blueScore,
        winningAlliance = winningAlliance,
    )

    @Test
    fun `played match names alliances, scores, bonus RP and winner`() {
        val bonuses =
            AllianceRpBonuses(red = listOf(true, true, true), blue = listOf(false, true, false))
        assertEquals(
            "Qual 1. Red 6324, 4907, 9496, 251 points, 3 of 3 bonus ranking points. " +
                "Blue 254, 1678, 118, 180 points, 1 of 3 bonus ranking points. Red wins.",
            matchRowDescription(
                match(),
                "Qual 1",
                bonuses,
                scheduledTime = null,
                isEstimate = false,
            ),
        )
    }

    @Test
    fun `blue win and tie`() {
        assertEquals(
            "Qual 1. Red 6324, 4907, 9496, 100 points. Blue 254, 1678, 118, 120 points. Blue wins.",
            matchRowDescription(match(100, 120, "blue"), "Qual 1", null, null, false),
        )
        assertEquals(
            "Qual 1. Red 6324, 4907, 9496, 90 points. Blue 254, 1678, 118, 90 points. Tie.",
            matchRowDescription(match(90, 90, ""), "Qual 1", null, null, false),
        )
    }

    @Test
    fun `unplayed match reads its scheduled time`() {
        assertEquals(
            "Qual 1. Red 6324, 4907, 9496. Blue 254, 1678, 118. Scheduled Saturday 9:25 AM (estimated).",
            matchRowDescription(match(-1, -1, null), "Qual 1", null, "Saturday 9:25 AM", true),
        )
        assertEquals(
            "Qual 1. Red 6324, 4907, 9496. Blue 254, 1678, 118. Not yet scheduled.",
            matchRowDescription(match(-1, -1, null), "Qual 1", null, null, false),
        )
    }

    @Test
    fun `surrogates and playoff alliances are spoken`() {
        val alliance =
            Alliance(
                eventKey = "2026test",
                number = 3,
                name = null,
                picks = emptyList(),
                declines = emptyList(),
                backupIn = null,
                backupOut = null,
            )
        assertEquals(
            "Qual 1. Red (Alliance 3) 6324 surrogate, 4907, 9496, 251 points. " +
                "Blue 254, 1678, 118, 180 points. Red wins.",
            matchRowDescription(
                match(redSurrogates = listOf("frc6324"), redAlliance = alliance),
                "Qual 1",
                null,
                null,
                false,
            ),
        )
    }

    @Test
    fun `bonus description counts achieved dots`() {
        assertEquals("0 of 2 bonus ranking points", rpBonusDescription(listOf(false, false)))
    }
}
