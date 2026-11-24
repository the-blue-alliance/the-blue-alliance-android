package com.thebluealliance.android.ui.matches

/** Spoken score; a negative score means the match has not been played. */
internal fun scoreDescription(score: Int): String = if (score < 0) "Not played" else "$score points"

/** Spoken state of [alliance]'s score column, which shows the win only in bold. */
internal fun allianceScoreState(
    winningAlliance: String?,
    alliance: String,
): String? = if (winningAlliance == alliance) "Winner" else null
