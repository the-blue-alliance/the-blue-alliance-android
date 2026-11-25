package com.thebluealliance.android.ui.matches

/** Spoken score; a negative score means the match has not been played. */
internal fun scoreDescription(score: Int): String = if (score < 0) "Not played" else "$score points"

/**
 * Spoken state of [alliance]'s score column, which shows the win only in bold. A played match
 * with no winner is a tie.
 */
internal fun allianceScoreState(
    winningAlliance: String?,
    alliance: String,
    score: Int,
): String? =
    when {
        winningAlliance == alliance -> "Winner"
        winningAlliance == "" && score >= 0 -> "Tie"
        else -> null
    }

/**
 * Spoken label for a score column titled by its playoff alliance, so it still names the
 * [alliance] colour of the team links that follow; null when the title is the colour itself.
 */
internal fun allianceLabelDescription(
    playoffAllianceTitle: String?,
    alliance: String,
): String? =
    playoffAllianceTitle?.let {
        "$it, ${alliance.replaceFirstChar { c ->
            c.titlecase()
        }}"
    }
