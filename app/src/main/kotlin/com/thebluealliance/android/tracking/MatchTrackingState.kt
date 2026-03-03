package com.thebluealliance.android.tracking

import com.thebluealliance.android.domain.model.CompLevel
import com.thebluealliance.android.domain.model.Match
import com.thebluealliance.android.domain.model.PlayoffType

data class TrackedTeamState(
    val teamKey: String,
    val eventKey: String,
    val playoffType: PlayoffType,
    val nextMatch: Match?,
    val currentMatch: Match?,
    val lastMatch: Match?,
    val isTeamPlaying: Boolean,
    /** Qual record, shown once all quals are played. */
    val record: TeamRecord?,
    /** Millis timestamp after which the tracker should auto-dismiss (null = don't auto-dismiss). */
    val autoDismissAfter: Long?,
)

data class TeamRecord(
    val wins: Int,
    val losses: Int,
    val ties: Int,
) {
    override fun toString(): String = "$wins-$losses-$ties"
}

/**
 * Computes the tracked team state from a full match list.
 *
 * @param matches All matches for the event, in play order
 * @param teamKey The team to track (e.g., "frc175")
 * @param currentTimeMillis Current wall-clock time in millis since epoch
 * @param currentMatchKey Optional override: the match key currently being played at the event
 */
fun computeTrackedTeamState(
    matches: List<Match>,
    teamKey: String,
    eventKey: String,
    playoffType: PlayoffType = PlayoffType.OTHER,
    currentTimeMillis: Long,
    currentMatchKey: String? = null,
): TrackedTeamState {
    val sorted = matches.sortedWith(matchPlayOrder)

    // Find the "current match" — the match being played right now at the event.
    // If an explicit currentMatchKey is provided, use that.
    // Otherwise, heuristic: find the last match with no actual result whose
    // predicted/scheduled time has passed.
    val currentMatch = if (currentMatchKey != null) {
        sorted.find { it.key == currentMatchKey }
    } else {
        findCurrentMatch(sorted, currentTimeMillis)
    }

    // "Last" = most recent completed match involving the tracked team
    val lastMatch = sorted
        .filter { it.containsTeam(teamKey) && it.hasBeenPlayed() }
        .lastOrNull()

    // "Next" = first unplayed match involving the tracked team after current
    val nextMatch = sorted
        .filter { it.containsTeam(teamKey) && !it.hasBeenPlayed() }
        .let { unplayed ->
            if (currentMatch != null) {
                // Exclude the current match from "next" — it could be this team's match
                unplayed.filter { it.key != currentMatch.key }
            } else {
                unplayed
            }
        }
        .firstOrNull()

    val isTeamPlaying = currentMatch != null && currentMatch.containsTeam(teamKey)

    // Compute record from quals only
    val record = computeRecord(sorted, teamKey)

    // Show qual record once all quals for this team are played
    val teamQuals = sorted.filter { it.containsTeam(teamKey) && it.compLevel == CompLevel.QUAL }
    val allQualsPlayed = teamQuals.isNotEmpty() && teamQuals.all { it.hasBeenPlayed() }
    val showRecord = allQualsPlayed && record != null && record.wins + record.losses + record.ties > 0

    // Auto-dismiss: 2h after the last team match time, when there's nothing left to show
    val autoDismissAfter = if (nextMatch == null && currentMatch == null && lastMatch != null) {
        val lastTime = lastMatch.actualTime ?: lastMatch.predictedTime ?: lastMatch.time
        lastTime?.let { (it * 1000) + AUTO_DISMISS_DELAY_MS }
    } else null

    return TrackedTeamState(
        teamKey = teamKey,
        eventKey = eventKey,
        playoffType = playoffType,
        nextMatch = nextMatch,
        currentMatch = currentMatch,
        lastMatch = lastMatch,
        isTeamPlaying = isTeamPlaying,
        record = if (showRecord) record else null,
        autoDismissAfter = autoDismissAfter,
    )
}

private fun findCurrentMatch(sorted: List<Match>, currentTimeMillis: Long): Match? {
    // The current match is the latest match that:
    // 1. Has NOT been played (no actual result)
    // 2. Has a time (predicted or scheduled) that has passed
    // OR if no times are available, return null
    val unplayed = sorted.filter { !it.hasBeenPlayed() }
    return unplayed.lastOrNull { match ->
        val matchTime = match.predictedTime ?: match.time
        matchTime != null && matchTime * 1000 <= currentTimeMillis
    }
}

private fun computeRecord(matches: List<Match>, teamKey: String): TeamRecord? {
    var wins = 0
    var losses = 0
    var ties = 0
    var hasQuals = false
    for (match in matches) {
        if (match.compLevel != CompLevel.QUAL) continue
        if (!match.containsTeam(teamKey)) continue
        if (!match.hasBeenPlayed()) continue
        hasQuals = true
        val teamAlliance = if (match.redTeamKeys.contains(teamKey)) "red" else "blue"
        when {
            match.winningAlliance == teamAlliance -> wins++
            match.winningAlliance == null || match.winningAlliance.isEmpty() -> ties++
            match.redScore == match.blueScore -> ties++
            else -> losses++
        }
    }
    return if (hasQuals) TeamRecord(wins, losses, ties) else null
}

fun Match.containsTeam(teamKey: String): Boolean =
    redTeamKeys.contains(teamKey) || blueTeamKeys.contains(teamKey)

fun Match.hasBeenPlayed(): Boolean =
    winningAlliance != null || (redScore >= 0 && blueScore >= 0 && (redScore > 0 || blueScore > 0))

/** Auto-dismiss delay: 2 hours after the last match time. */
private const val AUTO_DISMISS_DELAY_MS = 2 * 60 * 60 * 1000L

val matchPlayOrder: Comparator<Match> = compareBy<Match> { it.compLevel.order }
    .thenBy { it.setNumber }
    .thenBy { it.matchNumber }
