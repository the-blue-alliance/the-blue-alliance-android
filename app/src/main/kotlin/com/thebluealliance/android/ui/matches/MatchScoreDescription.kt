package com.thebluealliance.android.ui.matches

import com.thebluealliance.android.R
import com.thebluealliance.android.domain.BREAKDOWN_FOUL_SIGN
import com.thebluealliance.android.domain.BREAKDOWN_MISSING
import com.thebluealliance.android.domain.BREAKDOWN_NO
import com.thebluealliance.android.domain.BREAKDOWN_RP_BONUS_MET
import com.thebluealliance.android.domain.BREAKDOWN_RP_PREFIX
import com.thebluealliance.android.domain.BREAKDOWN_RP_SUFFIX
import com.thebluealliance.android.domain.BREAKDOWN_YES
import com.thebluealliance.android.ui.common.QuantityStrings

/** Spoken score; a negative score means the match has not been played. */
internal fun scoreDescription(
    score: Int,
    strings: QuantityStrings,
): String = if (score < 0) "Not played" else strings.get(R.plurals.points, score)

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

/**
 * One spoken line for a breakdown row, naming each value's alliance since only colour shows it.
 * Playoff alliances are named as in their score column; glyphs and abbreviations read as words.
 */
internal fun breakdownRowDescription(
    label: String,
    redValue: String,
    blueValue: String,
    strings: QuantityStrings,
    redPlayoffAllianceTitle: String? = null,
    bluePlayoffAllianceTitle: String? = null,
): String {
    val red = allianceLabelDescription(redPlayoffAllianceTitle, "red") ?: "Red"
    val blue = allianceLabelDescription(bluePlayoffAllianceTitle, "blue") ?: "Blue"
    val redSpoken = spokenBreakdownValue(redValue, strings)
    val blueSpoken = spokenBreakdownValue(blueValue, strings)
    return "$label: $red $redSpoken; $blue $blueSpoken"
}

/** A displayed breakdown value as TalkBack should say it. */
internal fun spokenBreakdownValue(
    value: String,
    strings: QuantityStrings,
): String {
    val rpCount =
        value
            .takeIf { it.startsWith(BREAKDOWN_RP_PREFIX) && it.endsWith(BREAKDOWN_RP_SUFFIX) }
            ?.removePrefix(BREAKDOWN_RP_PREFIX)
            ?.removeSuffix(BREAKDOWN_RP_SUFFIX)
            ?.toIntOrNull()
    return when {
        value == BREAKDOWN_MISSING -> "none"
        value == BREAKDOWN_YES -> "yes"
        value == BREAKDOWN_NO -> "no"
        value == BREAKDOWN_RP_BONUS_MET -> "yes, plus ${strings.get(R.plurals.ranking_points, 1)}"
        rpCount != null -> "plus ${strings.get(R.plurals.ranking_points, rpCount)}"
        value.startsWith(BREAKDOWN_FOUL_SIGN) -> "minus ${value.removePrefix(BREAKDOWN_FOUL_SIGN)}"
        else -> value
    }
}

/**
 * Spoken label for a team link, naming its alliance since only colour shows it: the playoff
 * alliance when there is one, otherwise the [alliance] colour.
 */
internal fun allianceTeamDescription(
    playoffAllianceTitle: String?,
    alliance: String,
    teamNumber: String,
): String {
    val label = playoffAllianceTitle ?: "${alliance.replaceFirstChar { it.titlecase() }} alliance"
    return "$label, team $teamNumber"
}
