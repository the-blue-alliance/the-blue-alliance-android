package com.thebluealliance.android.domain

import com.thebluealliance.android.domain.model.CompLevel
import com.thebluealliance.android.domain.model.Match
import com.thebluealliance.android.domain.model.PlayoffType

data class MatchAdvancementMsgs(
    val red: String,
    val blue: String,
)

/**
 * Helper function to returns the Advancement on each side of a Playoff Match
 * @param playoffType The type of playoff of this event
 * @return An MatchAdvancementMsg object or null
 */
fun Match.getAdvancement(playoffType: PlayoffType?): MatchAdvancementMsgs? {
    if (this.compLevel !in PLAYOFF_COMP_LEVELS || this.compLevel == CompLevel.FINAL) return null

    val (winAdvancement, loseAdvancement) = when (playoffType) {
        // 2023+
        PlayoffType.DOUBLE_ELIM_8_TEAM -> when (this.setNumber) {
            1, 2 -> "Advances to Upper Bracket - R2-7" to "Advances to Lower Bracket - R2-5"
            3, 4 -> "Advances to Upper Bracket - R2-8" to "Advances to Lower Bracket - R2-6"
            5 -> "Advances to Lower Bracket - R3-10" to "Eliminated"
            6 -> "Advances to Lower Bracket - R3-9" to "Eliminated"
            7 -> "Advances to Upper Bracket - R4-12" to "Advances to Lower Bracket - R3-9"
            8 -> "Advances to Upper Bracket - R4-12" to "Advances to Lower Bracket - R3-10"
            9 -> "Advances to Lower Bracket - R4-11" to "Eliminated"
            10 -> "Advances to Lower Bracket - R4-11" to "Eliminated"
            11 -> "Advances to Lower Bracket - R5-13" to "Eliminated"
            12 -> "Advances to Finals" to "Advances to Lower Bracket - R5-13"
            13 -> "Advances to Finals" to "Eliminated"
            else -> null
        }
        PlayoffType.DOUBLE_ELIM_4_TEAM -> when (this.setNumber) {
            1, 2 -> "Advances to Upper Bracket - R2-3" to "Advances to Lower Bracket - R2-4"
            3 -> "Advances to Finals" to "Advances to Lower Bracket - R3-5"
            4 -> "Advances to Lower Bracket - R3-5" to "Eliminated"
            5 -> "Advances to Finals" to "Eliminated"
            else -> null
        }
        else -> null
    }?: return null

    return MatchAdvancementMsgs(
        red = if (this.winningAlliance == "red") winAdvancement else loseAdvancement,
        blue = if (this.winningAlliance == "blue") winAdvancement else loseAdvancement,
    )
}
