package com.thebluealliance.android.tracking

import com.thebluealliance.android.domain.model.CompLevel
import com.thebluealliance.android.domain.model.Match
import com.thebluealliance.android.domain.model.PlayoffType
import com.thebluealliance.android.tracking.MatchTrackingService.Companion.POLL_FAST_MS
import com.thebluealliance.android.tracking.MatchTrackingService.Companion.POLL_MEDIUM_MS
import com.thebluealliance.android.tracking.MatchTrackingService.Companion.POLL_SLOW_MS
import com.thebluealliance.android.tracking.MatchTrackingService.Companion.TICKER_DEFAULT_MS
import com.thebluealliance.android.tracking.MatchTrackingService.Companion.TICKER_MIN_MS
import com.thebluealliance.android.tracking.MatchTrackingService.Companion.computeNextPollDelay
import com.thebluealliance.android.tracking.MatchTrackingService.Companion.computeNextTickerDelay
import com.thebluealliance.android.tracking.MatchTrackingService.Companion.shouldEnterDormant
import java.time.LocalDate
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class MatchTrackingServiceTest {

    private val teamKey = "frc175"
    private val eventKey = "2026test"

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

    private fun state(
        nextMatch: Match? = null,
        currentMatch: Match? = null,
        lastMatch: Match? = null,
    ) = TrackedTeamState(
        teamKey = teamKey,
        eventKey = eventKey,
        playoffType = PlayoffType.OTHER,
        nextMatch = nextMatch,
        currentMatch = currentMatch,
        lastMatch = lastMatch,
        isTeamPlaying = currentMatch?.let { it.redTeamKeys.contains(teamKey) || it.blueTeamKeys.contains(teamKey) } ?: false,
        record = null,
        autoDismissAfter = null,
    )

    // ── computeNextPollDelay ──

    @Test
    fun `poll - null state returns fast`() {
        assertEquals(POLL_FAST_MS, computeNextPollDelay(null))
    }

    @Test
    fun `poll - match in progress returns fast`() {
        val s = state(currentMatch = match(1, time = 1000))
        assertEquals(POLL_FAST_MS, computeNextPollDelay(s, now = 1_100_000))
    }

    @Test
    fun `poll - next match less than 15 min away returns fast`() {
        val nextTime = 2000L // seconds
        val now = (nextTime * 1000) - (10 * 60_000) // 10 min before
        val s = state(nextMatch = match(1, time = nextTime))
        assertEquals(POLL_FAST_MS, computeNextPollDelay(s, now = now))
    }

    @Test
    fun `poll - next match 30 min away returns medium`() {
        val nextTime = 2000L
        val now = (nextTime * 1000) - (30 * 60_000) // 30 min before
        val s = state(nextMatch = match(1, time = nextTime))
        assertEquals(POLL_MEDIUM_MS, computeNextPollDelay(s, now = now))
    }

    @Test
    fun `poll - next match 2 hours away returns slow`() {
        val nextTime = 10_000L
        val now = (nextTime * 1000) - (2 * 60 * 60_000L) // 2h before
        val s = state(nextMatch = match(1, time = nextTime))
        assertEquals(POLL_SLOW_MS, computeNextPollDelay(s, now = now))
    }

    @Test
    fun `poll - no next match with last match returns medium (waiting for schedule)`() {
        val s = state(nextMatch = null, lastMatch = match(1, time = 1000, redScore = 50, blueScore = 40, winningAlliance = "red"))
        assertEquals(POLL_MEDIUM_MS, computeNextPollDelay(s, now = 2_000_000))
    }

    @Test
    fun `poll - no next match and no last match returns slow`() {
        val s = state(nextMatch = null, lastMatch = null)
        assertEquals(POLL_SLOW_MS, computeNextPollDelay(s, now = 2_000_000))
    }

    @Test
    fun `poll - next match with no time returns fast (need to discover time)`() {
        val s = state(nextMatch = match(1, time = null, predictedTime = null))
        assertEquals(POLL_FAST_MS, computeNextPollDelay(s, now = 1_000_000))
    }

    @Test
    fun `poll - uses predictedTime over time`() {
        val scheduledTime = 10_000L  // far away
        val predictedTime = 2000L    // close
        val now = (predictedTime * 1000) - (5 * 60_000) // 5 min before predicted
        val s = state(nextMatch = match(1, time = scheduledTime, predictedTime = predictedTime))
        assertEquals(POLL_FAST_MS, computeNextPollDelay(s, now = now))
    }

    @Test
    fun `poll - at exactly 15 min boundary returns fast`() {
        val nextTime = 2000L
        val now = (nextTime * 1000) - (15 * 60_000) // exactly 15 min
        val s = state(nextMatch = match(1, time = nextTime))
        // timeToNext == 15 min, which is NOT < 15 min, so medium
        assertEquals(POLL_MEDIUM_MS, computeNextPollDelay(s, now = now))
    }

    @Test
    fun `poll - at exactly 60 min boundary returns medium`() {
        val nextTime = 5000L
        val now = (nextTime * 1000) - (60 * 60_000L) // exactly 60 min
        val s = state(nextMatch = match(1, time = nextTime))
        // timeToNext == 60 min, which is NOT < 60 min, so slow
        assertEquals(POLL_SLOW_MS, computeNextPollDelay(s, now = now))
    }

    // ── computeNextTickerDelay ──

    @Test
    fun `ticker - null state returns default`() {
        assertEquals(TICKER_DEFAULT_MS, computeNextTickerDelay(null))
    }

    @Test
    fun `ticker - match in progress returns min`() {
        val s = state(currentMatch = match(1, time = 1000))
        assertEquals(TICKER_MIN_MS, computeNextTickerDelay(s, now = 1_100_000))
    }

    @Test
    fun `ticker - no next match returns default`() {
        val s = state(nextMatch = null)
        assertEquals(TICKER_DEFAULT_MS, computeNextTickerDelay(s, now = 1_000_000))
    }

    @Test
    fun `ticker - next match with no time returns default`() {
        val s = state(nextMatch = match(1, time = null))
        assertEquals(TICKER_DEFAULT_MS, computeNextTickerDelay(s, now = 1_000_000))
    }

    @Test
    fun `ticker - next match far away returns default`() {
        val nextTime = 10_000L
        val now = (nextTime * 1000) - (30 * 60_000L) // 30 min before
        val s = state(nextMatch = match(1, time = nextTime))
        // timeToNext=30min, wakeBeforeMatch=29min → clamped to 5min default
        assertEquals(TICKER_DEFAULT_MS, computeNextTickerDelay(s, now = now))
    }

    @Test
    fun `ticker - next match 3 min away wakes 1 min before`() {
        val nextTime = 2000L
        val now = (nextTime * 1000) - (3 * 60_000) // 3 min before
        val s = state(nextMatch = match(1, time = nextTime))
        // timeToNext=3min, wakeBeforeMatch=2min → 2min (between 60s and 5min)
        assertEquals(2 * 60_000L, computeNextTickerDelay(s, now = now))
    }

    @Test
    fun `ticker - next match 90s away floors at 60s`() {
        val nextTime = 2000L
        val now = (nextTime * 1000) - (90_000) // 90s before
        val s = state(nextMatch = match(1, time = nextTime))
        // timeToNext=90s, wakeBeforeMatch=30s → floored to 60s
        assertEquals(TICKER_MIN_MS, computeNextTickerDelay(s, now = now))
    }

    @Test
    fun `ticker - next match already past floors at 60s`() {
        val nextTime = 2000L
        val now = (nextTime * 1000) + 60_000 // 1 min past
        val s = state(nextMatch = match(1, time = nextTime))
        // timeToNext=-60s, wakeBeforeMatch=-120s → floored to 60s
        assertEquals(TICKER_MIN_MS, computeNextTickerDelay(s, now = now))
    }

    @Test
    fun `ticker - uses predictedTime over time`() {
        val scheduledTime = 10_000L  // far away
        val predictedTime = 2000L
        val now = (predictedTime * 1000) - (3 * 60_000) // 3 min before predicted
        val s = state(nextMatch = match(1, time = scheduledTime, predictedTime = predictedTime))
        // Uses predictedTime: timeToNext=3min, wakeBeforeMatch=2min
        assertEquals(2 * 60_000L, computeNextTickerDelay(s, now = now))
    }

    @Test
    fun `ticker - exactly 6 min away clamps to default`() {
        val nextTime = 2000L
        val now = (nextTime * 1000) - (6 * 60_000L) // 6 min before
        val s = state(nextMatch = match(1, time = nextTime))
        // timeToNext=6min, wakeBeforeMatch=5min → exactly at TICKER_DEFAULT_MS
        assertEquals(TICKER_DEFAULT_MS, computeNextTickerDelay(s, now = now))
    }

    // ── shouldEnterDormant ──

    @Test
    fun `dormant - no endDate means stop, not dormant`() {
        assertFalse(shouldEnterDormant(eventEndDate = null, today = LocalDate.of(2026, 3, 6)))
    }

    @Test
    fun `dormant - today before endDate enters dormant`() {
        val endDate = LocalDate.of(2026, 3, 7) // Saturday
        val today = LocalDate.of(2026, 3, 6)   // Friday
        assertTrue(shouldEnterDormant(eventEndDate = endDate, today = today))
    }

    @Test
    fun `dormant - today equals endDate enters dormant`() {
        // Last day of event — might still have matches later today
        val endDate = LocalDate.of(2026, 3, 7)
        val today = LocalDate.of(2026, 3, 7)
        assertTrue(shouldEnterDormant(eventEndDate = endDate, today = today))
    }

    @Test
    fun `dormant - today after endDate means stop, not dormant`() {
        val endDate = LocalDate.of(2026, 3, 7)
        val today = LocalDate.of(2026, 3, 8) // Sunday, event is over
        assertFalse(shouldEnterDormant(eventEndDate = endDate, today = today))
    }
}
