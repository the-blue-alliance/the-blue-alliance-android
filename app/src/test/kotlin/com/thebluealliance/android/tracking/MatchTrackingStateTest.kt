package com.thebluealliance.android.tracking

import com.thebluealliance.android.domain.getShortLabel
import com.thebluealliance.android.domain.model.CompLevel
import com.thebluealliance.android.domain.model.Match
import com.thebluealliance.android.domain.model.PlayoffType
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class MatchTrackingStateTest {

    private val teamKey = "frc175"
    private val eventKey = "2026bcvi"

    // Helper to create matches quickly
    private fun match(
        number: Int,
        compLevel: CompLevel = CompLevel.QUAL,
        setNumber: Int = 1,
        time: Long? = null,
        predictedTime: Long? = null,
        actualTime: Long? = null,
        redTeams: List<String> = listOf("frc111", "frc222", "frc333"),
        blueTeams: List<String> = listOf("frc444", "frc555", "frc666"),
        redScore: Int = -1,
        blueScore: Int = -1,
        winningAlliance: String? = null,
    ) = Match(
        key = "${eventKey}_${compLevel.code}${number}",
        eventKey = eventKey,
        compLevel = compLevel,
        matchNumber = number,
        setNumber = setNumber,
        time = time,
        predictedTime = predictedTime,
        actualTime = actualTime,
        redTeamKeys = redTeams,
        blueTeamKeys = blueTeams,
        redScore = redScore,
        blueScore = blueScore,
        winningAlliance = winningAlliance,
    )

    @Test
    fun `no matches returns empty state`() {
        val state = computeTrackedTeamState(
            matches = emptyList(),
            teamKey = teamKey,
            eventKey = eventKey,
            currentTimeMillis = 1000000,
        )
        assertNull(state.nextMatch)
        assertNull(state.currentMatch)
        assertNull(state.lastMatch)
        assertFalse(state.isTeamPlaying)
        assertNull(state.record)
        assertNull(state.autoDismissAfter)
    }

    @Test
    fun `next match only - no matches played, none current`() {
        val futureTime = 2000L // in seconds
        val matches = listOf(
            match(1, time = futureTime, redTeams = listOf("frc175", "frc222", "frc333")),
            match(2, time = futureTime + 600),
        )

        val state = computeTrackedTeamState(
            matches = matches,
            teamKey = teamKey,
            eventKey = eventKey,
            currentTimeMillis = 1000L * 1000, // 1000 seconds in millis
        )

        assertNotNull(state.nextMatch)
        assertEquals(1, state.nextMatch!!.matchNumber)
        assertNull(state.currentMatch)
        assertNull(state.lastMatch)
        assertFalse(state.isTeamPlaying)
        assertNull(state.autoDismissAfter)
    }

    @Test
    fun `team's match is currently playing`() {
        val pastTime = 1000L
        val futureTime = 3000L
        val matches = listOf(
            match(1, time = pastTime, redScore = 100, blueScore = 90, winningAlliance = "red",
                redTeams = listOf("frc175", "frc222", "frc333")),
            match(2, time = pastTime + 600, // currently playing
                redTeams = listOf("frc175", "frc444", "frc555")),
            match(3, time = futureTime,
                redTeams = listOf("frc175", "frc666", "frc777")),
        )

        val state = computeTrackedTeamState(
            matches = matches,
            teamKey = teamKey,
            eventKey = eventKey,
            currentTimeMillis = (pastTime + 700) * 1000,
        )

        assertNotNull(state.currentMatch)
        assertEquals(2, state.currentMatch!!.matchNumber)
        assertTrue(state.isTeamPlaying)
        assertNotNull(state.nextMatch)
        assertEquals(3, state.nextMatch!!.matchNumber)
        assertNotNull(state.lastMatch)
        assertEquals(1, state.lastMatch!!.matchNumber)
    }

    @Test
    fun `current match is other team, tracked team not playing`() {
        val pastTime = 1000L
        val futureTime = 3000L
        val matches = listOf(
            match(1, time = pastTime, redScore = 100, blueScore = 90, winningAlliance = "red",
                redTeams = listOf("frc175", "frc222", "frc333")),
            match(2, time = pastTime + 600), // currently playing, team NOT in it
            match(3, time = futureTime,
                redTeams = listOf("frc175", "frc666", "frc777")),
        )

        val state = computeTrackedTeamState(
            matches = matches,
            teamKey = teamKey,
            eventKey = eventKey,
            currentTimeMillis = (pastTime + 700) * 1000,
        )

        assertNotNull(state.currentMatch)
        assertEquals(2, state.currentMatch!!.matchNumber)
        assertFalse(state.isTeamPlaying)
    }

    @Test
    fun `quals done without elims shows record and sets autoDismissAfter`() {
        val pastTime = 1000L
        val matches = listOf(
            match(1, time = pastTime, redScore = 100, blueScore = 90, winningAlliance = "red",
                redTeams = listOf("frc175", "frc222", "frc333")),
            match(2, time = pastTime + 600, redScore = 80, blueScore = 120, winningAlliance = "blue",
                blueTeams = listOf("frc175", "frc444", "frc555")),
            match(3, time = pastTime + 1200, redScore = 110, blueScore = 110, winningAlliance = null,
                redTeams = listOf("frc175", "frc666", "frc777")),
        )

        val state = computeTrackedTeamState(
            matches = matches,
            teamKey = teamKey,
            eventKey = eventKey,
            currentTimeMillis = (pastTime + 2000) * 1000,
        )

        assertNull(state.nextMatch)
        assertNotNull(state.lastMatch)
        assertNotNull(state.record)
        assertEquals(2, state.record!!.wins)
        assertEquals(0, state.record!!.losses)
        assertEquals(1, state.record!!.ties)
        // No next or current match, has last match → autoDismissAfter should be set
        assertNotNull(state.autoDismissAfter)
    }

    @Test
    fun `quals done then elims scheduled reverts to showing next`() {
        val pastTime = 1000L
        val futureTime = 5000L
        val matches = listOf(
            // All quals played
            match(1, time = pastTime, redScore = 100, blueScore = 90, winningAlliance = "red",
                redTeams = listOf("frc175", "frc222", "frc333")),
            match(2, time = pastTime + 600, redScore = 80, blueScore = 120, winningAlliance = "blue",
                blueTeams = listOf("frc175", "frc444", "frc555")),
            // Elim match scheduled (team got picked)
            match(1, compLevel = CompLevel.SEMIFINAL, setNumber = 1, time = futureTime,
                redTeams = listOf("frc175", "frc254", "frc1114")),
        )

        val state = computeTrackedTeamState(
            matches = matches,
            teamKey = teamKey,
            eventKey = eventKey,
            currentTimeMillis = (pastTime + 2000) * 1000,
        )

        // Should show next elim match
        assertNotNull(state.nextMatch)
        assertEquals(CompLevel.SEMIFINAL, state.nextMatch!!.compLevel)
        // Record should be shown (quals are done)
        assertNotNull(state.record)
        // Has next match → no auto-dismiss
        assertNull(state.autoDismissAfter)
    }

    @Test
    fun `all matches including elims complete`() {
        val pastTime = 1000L
        val matches = listOf(
            match(1, time = pastTime, redScore = 100, blueScore = 90, winningAlliance = "red",
                redTeams = listOf("frc175", "frc222", "frc333")),
            match(1, compLevel = CompLevel.FINAL, setNumber = 1,
                time = pastTime + 600, redScore = 150, blueScore = 120, winningAlliance = "red",
                redTeams = listOf("frc175", "frc254", "frc1114")),
        )

        val state = computeTrackedTeamState(
            matches = matches,
            teamKey = teamKey,
            eventKey = eventKey,
            currentTimeMillis = (pastTime + 2000) * 1000,
        )

        // No next or current match, has last → autoDismissAfter should be set
        assertNotNull(state.autoDismissAfter)
        assertNotNull(state.record)
    }

    @Test
    fun `record only counts quals`() {
        val pastTime = 1000L
        val matches = listOf(
            // Qual win
            match(1, time = pastTime, redScore = 100, blueScore = 90, winningAlliance = "red",
                redTeams = listOf("frc175", "frc222", "frc333")),
            // Semifinal win — should not count toward record
            match(1, compLevel = CompLevel.SEMIFINAL, setNumber = 1,
                time = pastTime + 600, redScore = 130, blueScore = 100, winningAlliance = "red",
                redTeams = listOf("frc175", "frc222", "frc333")),
        )

        val state = computeTrackedTeamState(
            matches = matches,
            teamKey = teamKey,
            eventKey = eventKey,
            currentTimeMillis = (pastTime + 2000) * 1000,
        )

        assertNotNull(state.record)
        assertEquals(1, state.record!!.wins)
        assertEquals(0, state.record!!.losses)
    }

    @Test
    fun `containsTeam works for both alliances`() {
        val m = match(1, redTeams = listOf("frc175", "frc222", "frc333"))
        assertTrue(m.containsTeam("frc175"))
        assertFalse(m.containsTeam("frc999"))

        val m2 = match(2, blueTeams = listOf("frc175", "frc222", "frc333"))
        assertTrue(m2.containsTeam("frc175"))
    }

    @Test
    fun `hasBeenPlayed detects played matches`() {
        val unplayed = match(1)
        assertFalse(unplayed.hasBeenPlayed())

        val played = match(2, redScore = 100, blueScore = 90, winningAlliance = "red")
        assertTrue(played.hasBeenPlayed())

        val tie = match(3, redScore = 100, blueScore = 100, winningAlliance = null)
        assertTrue(tie.hasBeenPlayed())
    }

    @Test
    fun `getShortLabel formats correctly for double elim`() {
        val doubleElim = PlayoffType.DOUBLE_ELIM_8_TEAM
        assertEquals("Q42", match(42, compLevel = CompLevel.QUAL).getShortLabel(doubleElim))
        assertEquals("R1-2", match(1, compLevel = CompLevel.SEMIFINAL, setNumber = 2).getShortLabel(doubleElim))
        assertEquals("F-1", match(1, compLevel = CompLevel.FINAL).getShortLabel(doubleElim))
    }

    @Test
    fun `explicit currentMatchKey overrides heuristic`() {
        val pastTime = 1000L
        val matches = listOf(
            match(1, time = pastTime, redTeams = listOf("frc175", "frc222", "frc333")),
            match(2, time = pastTime + 600),
            match(3, time = pastTime + 1200, redTeams = listOf("frc175", "frc666", "frc777")),
        )

        val state = computeTrackedTeamState(
            matches = matches,
            teamKey = teamKey,
            eventKey = eventKey,
            currentTimeMillis = (pastTime + 200) * 1000,
            currentMatchKey = "${eventKey}_qm2",
        )

        assertNotNull(state.currentMatch)
        assertEquals(2, state.currentMatch!!.matchNumber)
    }
}
