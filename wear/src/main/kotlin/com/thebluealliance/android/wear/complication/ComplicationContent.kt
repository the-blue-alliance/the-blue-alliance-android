package com.thebluealliance.android.wear.complication

import java.time.Duration
import java.time.Instant

/** The tracked team's data as stored by the app tracker, which the complication shows. */
internal data class TrackerSnapshot(
    val teamNumber: String,
    val eventName: String = "",
    val nextMatchLabel: String = "",
    val nextMatchTime: Instant? = null,
    val nextMatchIsEstimate: Boolean = false,
    val upcomingEventName: String = "",
    val upcomingEventDate: String = "",
)

/** What the complication shows. */
internal sealed interface ComplicationContent {
    /** No team is tracked yet. */
    data object NotConfigured : ComplicationContent

    /**
     * The team's next match. [matchTime] is when it starts, or null if unscheduled; the
     * complication counts down to it. [validUntil] is when to stop showing it, or null to keep it.
     */
    data class NextMatch(
        val teamNumber: String,
        val matchLabel: String,
        val matchTime: Instant?,
        val isEstimate: Boolean,
        val eventName: String,
        val validUntil: Instant?,
    ) : ComplicationContent

    /** The team's next event, when it isn't at one. */
    data class UpcomingEvent(
        val teamNumber: String,
        val eventName: String,
        val date: String,
    ) : ComplicationContent

    /** Just the team number. */
    data class TeamOnly(
        val teamNumber: String,
    ) : ComplicationContent
}

internal object ComplicationContentLogic {
    /**
     * How long after its scheduled time a next match keeps showing. Longer than the 15-minute
     * at-event refresh, so it only expires when refreshes stop arriving.
     */
    val NEXT_MATCH_GRACE: Duration = Duration.ofMinutes(30)

    /**
     * Each complication shows its own copy of the data, written only by the refresh worker for the
     * complications active when it last ran. A newly added complication has no copy yet, so it
     * shows only the team number, whatever the tracker holds.
     */
    @Suppress("UNUSED_PARAMETER")
    fun resolve(
        tracker: TrackerSnapshot,
        now: Instant,
    ): ComplicationContent =
        if (tracker.teamNumber.isBlank()) {
            ComplicationContent.NotConfigured
        } else {
            ComplicationContent.TeamOnly(tracker.teamNumber)
        }
}
