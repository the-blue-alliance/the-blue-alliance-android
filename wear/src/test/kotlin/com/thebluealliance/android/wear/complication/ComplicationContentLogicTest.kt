package com.thebluealliance.android.wear.complication

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.Duration
import java.time.Instant

class ComplicationContentLogicTest {
    private val now = Instant.parse("2027-03-20T18:00:00Z")
    private val matchTime = now.plus(Duration.ofMinutes(25))

    private val atEvent =
        TrackerSnapshot(
            teamNumber = "254",
            eventName = "Silicon Valley",
            nextMatchLabel = "Q51",
            nextMatchTime = matchTime,
            nextMatchIsEstimate = true,
        )

    @Test
    fun `a new complication shows the tracker's next match`() {
        assertEquals(
            ComplicationContent.NextMatch(
                teamNumber = "254",
                matchLabel = "Q51",
                matchTime = matchTime,
                isEstimate = true,
                eventName = "Silicon Valley",
                validUntil = matchTime.plus(ComplicationContentLogic.NEXT_MATCH_GRACE),
            ),
            ComplicationContentLogic.resolve(atEvent, now),
        )
    }

    @Test
    fun `an unscheduled next match shows without an expiry`() {
        val tracker = atEvent.copy(nextMatchTime = null, nextMatchIsEstimate = false)
        assertEquals(
            ComplicationContent.NextMatch(
                teamNumber = "254",
                matchLabel = "Q51",
                matchTime = null,
                isEstimate = false,
                eventName = "Silicon Valley",
                validUntil = null,
            ),
            ComplicationContentLogic.resolve(tracker, now),
        )
    }

    @Test
    fun `a new complication shows the tracker's upcoming event`() {
        val tracker =
            TrackerSnapshot(
                teamNumber = "254",
                upcomingEventName = "Hartford",
                upcomingEventDate = "Mar 27",
            )
        assertEquals(
            ComplicationContent.UpcomingEvent(
                teamNumber = "254",
                eventName = "Hartford",
                date = "Mar 27",
            ),
            ComplicationContentLogic.resolve(tracker, now),
        )
    }

    @Test
    fun `a next match well past its time is not shown`() {
        val later = matchTime.plus(ComplicationContentLogic.NEXT_MATCH_GRACE).plusSeconds(1)
        assertEquals(
            ComplicationContent.TeamOnly("254"),
            ComplicationContentLogic.resolve(atEvent, later),
        )
    }

    @Test
    fun `a team with nothing scheduled shows the team number`() {
        assertEquals(
            ComplicationContent.TeamOnly("254"),
            ComplicationContentLogic.resolve(TrackerSnapshot(teamNumber = "254"), now),
        )
    }

    @Test
    fun `no tracked team asks to be set up`() {
        assertEquals(
            ComplicationContent.NotConfigured,
            ComplicationContentLogic.resolve(TrackerSnapshot(teamNumber = ""), now),
        )
    }
}
