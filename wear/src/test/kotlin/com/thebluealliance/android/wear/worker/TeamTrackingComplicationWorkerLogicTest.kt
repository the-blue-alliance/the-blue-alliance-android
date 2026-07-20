package com.thebluealliance.android.wear.worker

import com.thebluealliance.android.data.remote.dto.MatchAllianceDto
import com.thebluealliance.android.data.remote.dto.MatchAlliancesDto
import com.thebluealliance.android.data.remote.dto.MatchDto
import com.thebluealliance.android.wear.tracker.Alliance
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonObject
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * Unit tests for the pure match/record logic in [MatchLogic]. These helpers do the parsing and
 * edge-case work that breaks silently, so they're worth covering directly.
 */
class TeamTrackingComplicationWorkerLogicTest {
    private val logic = MatchLogic

    private fun match(
        compLevel: String = "qm",
        matchNumber: Int = 1,
        setNumber: Int = 1,
        redTeams: List<String> = emptyList(),
        blueTeams: List<String> = emptyList(),
        redScore: Int = -1,
        blueScore: Int = -1,
        winningAlliance: String? = null,
        scoreBreakdown: JsonObject? = null,
        time: Long? = null,
        predictedTime: Long? = null,
    ) = MatchDto(
        key = "2024test_${compLevel}$setNumber-$matchNumber",
        eventKey = "2024test",
        compLevel = compLevel,
        matchNumber = matchNumber,
        setNumber = setNumber,
        alliances =
            MatchAlliancesDto(
                red = MatchAllianceDto(score = redScore, teamKeys = redTeams),
                blue = MatchAllianceDto(score = blueScore, teamKeys = blueTeams),
            ),
        scoreBreakdown = scoreBreakdown,
        time = time,
        predictedTime = predictedTime,
        winningAlliance = winningAlliance,
    )

    // region compLevelOrder

    @Test
    fun `compLevelOrder ranks elimination rounds after quals`() {
        assertEquals(0, logic.compLevelOrder("qm"))
        assertEquals(1, logic.compLevelOrder("ef"))
        assertEquals(2, logic.compLevelOrder("qf"))
        assertEquals(3, logic.compLevelOrder("sf"))
        assertEquals(4, logic.compLevelOrder("f"))
    }

    @Test
    fun `compLevelOrder sorts unknown comp levels last`() {
        assertEquals(Int.MAX_VALUE, logic.compLevelOrder("unknown"))
        assertEquals(Int.MAX_VALUE, logic.compLevelOrder(""))
    }

    // endregion

    // region getShortLabel

    @Test
    fun `getShortLabel uses Q + match number for quals`() {
        assertEquals("Q18", logic.getShortLabel(match(compLevel = "qm", matchNumber = 18)))
    }

    @Test
    fun `getShortLabel uses set-match for elimination rounds`() {
        assertEquals(
            "SF2-1",
            logic.getShortLabel(match(compLevel = "sf", setNumber = 2, matchNumber = 1)),
        )
        assertEquals(
            "F1-3",
            logic.getShortLabel(match(compLevel = "f", setNumber = 1, matchNumber = 3)),
        )
        assertEquals(
            "QF4-2",
            logic.getShortLabel(match(compLevel = "qf", setNumber = 4, matchNumber = 2)),
        )
        assertEquals(
            "EF1-1",
            logic.getShortLabel(match(compLevel = "ef", setNumber = 1, matchNumber = 1)),
        )
    }

    @Test
    fun `getShortLabel falls back to raw comp level for unknown rounds`() {
        assertEquals(
            "xx3-5",
            logic.getShortLabel(match(compLevel = "xx", setNumber = 3, matchNumber = 5)),
        )
    }

    // endregion

    // region qualRecord

    @Test
    fun `qualRecord tallies wins losses and ties over played quals`() {
        val team = "frc254"
        val matches =
            listOf(
                // Win: on red, red won
                match(
                    redTeams = listOf(team),
                    redScore = 100,
                    blueScore = 50,
                    winningAlliance = "red",
                ),
                // Loss: on blue, red won
                match(
                    blueTeams = listOf(team),
                    redScore = 100,
                    blueScore = 50,
                    winningAlliance = "red",
                ),
                // Tie: played, no winning alliance
                match(redTeams = listOf(team), redScore = 50, blueScore = 50, winningAlliance = ""),
                // Ignored: unplayed (score < 0)
                match(redTeams = listOf(team), redScore = -1),
                // Ignored: playoff match, not a qual
                match(
                    compLevel = "sf",
                    redTeams = listOf(team),
                    redScore = 100,
                    winningAlliance = "red",
                ),
            )
        assertEquals("1-1-1", logic.qualRecord(matches, team))
    }

    @Test
    fun `qualRecord treats a null winning alliance as a tie`() {
        val team = "frc111"
        val matches =
            listOf(
                match(
                    redTeams = listOf(team),
                    redScore = 40,
                    blueScore = 40,
                    winningAlliance = null,
                ),
            )
        assertEquals("0-0-1", logic.qualRecord(matches, team))
    }

    @Test
    fun `qualRecord is 0-0-0 when no quals have been played`() {
        assertEquals("0-0-0", logic.qualRecord(emptyList(), "frc1"))
    }

    // endregion

    // region extractBonusRp

    private fun breakdownWithRp(rp: Int): JsonObject =
        buildJsonObject { putJsonObject("red") { put("rp", rp) } }

    @Test
    fun `extractBonusRp subtracts win RP from total for a winning alliance`() {
        val m =
            match(
                redTeams = listOf("frc1"),
                winningAlliance = "red",
                scoreBreakdown = breakdownWithRp(4),
            )
        assertEquals(2, logic.extractBonusRp(m, Alliance.RED))
    }

    @Test
    fun `extractBonusRp subtracts one tie RP when there is no winner`() {
        val m = match(winningAlliance = "", scoreBreakdown = breakdownWithRp(3))
        assertEquals(2, logic.extractBonusRp(m, Alliance.RED))
    }

    @Test
    fun `extractBonusRp subtracts no RP for a losing alliance`() {
        val m = match(winningAlliance = "blue", scoreBreakdown = breakdownWithRp(1))
        assertEquals(1, logic.extractBonusRp(m, Alliance.RED))
    }

    @Test
    fun `extractBonusRp never returns negative`() {
        // rp (1) minus win RP (2) would be -1; coerced to 0.
        val m = match(winningAlliance = "red", scoreBreakdown = breakdownWithRp(1))
        assertEquals(0, logic.extractBonusRp(m, Alliance.RED))
    }

    @Test
    fun `extractBonusRp is zero for a null alliance`() {
        assertEquals(0, logic.extractBonusRp(match(scoreBreakdown = breakdownWithRp(4)), null))
    }

    @Test
    fun `extractBonusRp is zero when there is no score breakdown`() {
        assertEquals(0, logic.extractBonusRp(match(), Alliance.RED))
    }

    @Test
    fun `extractBonusRp is zero when the rp field is absent`() {
        val m =
            match(
                scoreBreakdown = buildJsonObject { putJsonObject("red") { put("foulPoints", 0) } },
            )
        assertEquals(0, logic.extractBonusRp(m, Alliance.RED))
    }

    // endregion

    // region formatMatchTime

    @Test
    fun `formatMatchTime is TBD when no time is known`() {
        assertEquals(
            "TBD",
            logic.formatMatchTime(
                match(time = null, predictedTime = null),
                includeEstimatePrefix = true,
            ),
        )
    }

    @Test
    fun `formatMatchTime prefixes a predicted time today with a tilde when requested`() {
        val now = Instant.now().epochSecond
        val result = logic.formatMatchTime(match(predictedTime = now), includeEstimatePrefix = true)
        assertTrue(result.startsWith("~"), "expected estimate prefix, got '$result'")
    }

    @Test
    fun `formatMatchTime omits the tilde for a predicted time when not requested`() {
        val now = Instant.now().epochSecond
        val result =
            logic.formatMatchTime(
                match(predictedTime = now),
                includeEstimatePrefix = false,
            )
        assertTrue(result.first().isDigit(), "expected a clock time with no prefix, got '$result'")
    }

    @Test
    fun `formatMatchTime omits the tilde for an official time today`() {
        val now = Instant.now().epochSecond
        val result =
            logic.formatMatchTime(
                match(time = now, predictedTime = null),
                includeEstimatePrefix = true,
            )
        assertTrue(result.first().isDigit(), "expected a clock time, got '$result'")
    }

    @Test
    fun `formatMatchTime shows a weekday abbreviation for matches on other days`() {
        // ~30 days ago is definitely not today, so the day-of-week branch runs. Compare against
        // the same formatter so the assertion is locale-independent.
        val past = Instant.now().epochSecond - 30L * 24 * 60 * 60
        val expected =
            Instant
                .ofEpochSecond(past)
                .atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("EEE"))
        assertEquals(
            expected,
            logic.formatMatchTime(match(time = past), includeEstimatePrefix = false),
        )
    }

    // endregion
}
