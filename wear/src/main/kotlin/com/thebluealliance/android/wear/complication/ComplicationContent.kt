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

    fun resolve(
        tracker: TrackerSnapshot,
        now: Instant,
    ): ComplicationContent {
        if (tracker.teamNumber.isBlank()) return ComplicationContent.NotConfigured
        if (tracker.nextMatchLabel.isNotBlank()) {
            val validUntil = tracker.nextMatchTime?.plus(NEXT_MATCH_GRACE)
            // Past its time and no refresh has replaced it: the match has been played, so don't
            // keep presenting it as "next".
            if (validUntil == null || now <= validUntil) {
                return ComplicationContent.NextMatch(
                    teamNumber = tracker.teamNumber,
                    matchLabel = tracker.nextMatchLabel,
                    matchTime = tracker.nextMatchTime,
                    isEstimate = tracker.nextMatchIsEstimate,
                    eventName = tracker.eventName,
                    validUntil = validUntil,
                )
            }
        }
        if (tracker.upcomingEventName.isNotBlank()) {
            return ComplicationContent.UpcomingEvent(
                teamNumber = tracker.teamNumber,
                eventName = tracker.upcomingEventName,
                date = tracker.upcomingEventDate,
            )
        }
        return ComplicationContent.TeamOnly(tracker.teamNumber)
    }
}
