package com.thebluealliance.android.ui.components

import com.thebluealliance.android.R
import com.thebluealliance.android.domain.AllianceRpBonuses
import com.thebluealliance.android.domain.model.Alliance
import com.thebluealliance.android.domain.model.Match
import com.thebluealliance.android.domain.model.displayTitle
import com.thebluealliance.android.ui.common.QuantityStrings
import com.thebluealliance.android.util.teamNumber

/**
 * One spoken summary of a match row, so screen readers and semantics-based targeting get the
 * alliances, scores and winner that the row shows only through color and weight.
 *
 * [scheduledTime] is the already-formatted time shown for an unplayed match, or null if unknown.
 */
internal fun matchRowDescription(
    match: Match,
    label: String,
    rpBonuses: AllianceRpBonuses?,
    scheduledTime: String?,
    isEstimate: Boolean,
    strings: QuantityStrings,
): String {
    val played = match.redScore >= 0
    return buildString {
        append(label)
        append(". ")
        append(
            allianceDescription(
                strings = strings,
                color = "Red",
                alliance = match.redPlayoffAlliance,
                teamKeys = match.redTeamKeys,
                surrogateKeys = match.redSurrogateTeamKeys,
                score = match.redScore.takeIf { played },
                bonuses = rpBonuses?.red,
            ),
        )
        append(" ")
        append(
            allianceDescription(
                strings = strings,
                color = "Blue",
                alliance = match.bluePlayoffAlliance,
                teamKeys = match.blueTeamKeys,
                surrogateKeys = match.blueSurrogateTeamKeys,
                score = match.blueScore.takeIf { played },
                bonuses = rpBonuses?.blue,
            ),
        )
        append(" ")
        when {
            !played && scheduledTime == null -> append("Not yet scheduled.")
            !played -> append("Scheduled $scheduledTime${if (isEstimate) " (estimated)" else ""}.")
            match.winningAlliance == "red" -> append("Red wins.")
            match.winningAlliance == "blue" -> append("Blue wins.")
            else -> append("Tie.")
        }
    }
}

/** Spoken count of achieved bonus ranking points, e.g. "2 of 3 bonus ranking points". */
internal fun rpBonusDescription(bonuses: List<Boolean>): String =
    "${bonuses.count { it }} of ${bonuses.size} bonus ranking points"

private fun allianceDescription(
    strings: QuantityStrings,
    color: String,
    alliance: Alliance?,
    teamKeys: List<String>,
    surrogateKeys: List<String>,
    score: Int?,
    bonuses: List<Boolean>?,
): String =
    buildString {
        append(color)
        alliance?.let { append(" (${it.displayTitle})") }
        append(" ")
        append(
            teamKeys.joinToString(", ") { key ->
                if (key in surrogateKeys) "${key.teamNumber} surrogate" else key.teamNumber
            },
        )
        if (score != null) append(", ${strings.get(R.plurals.points, score)}")
        if (bonuses != null) append(", ${rpBonusDescription(bonuses)}")
        append(".")
    }
