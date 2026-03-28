package com.thebluealliance.android.domain.model

import com.thebluealliance.android.domain.PLAYOFF_COMP_LEVELS

data class Match(
    val key: String,
    val eventKey: String,
    val compLevel: CompLevel,
    val matchNumber: Int,
    val setNumber: Int,
    val time: Long?,
    val predictedTime: Long?,
    val actualTime: Long?,
    val redTeamKeys: List<String>,
    val redScore: Int,
    val redAdvancement: String? = null,
    val redPlayoffAlliance: Alliance? = null,
    val blueTeamKeys: List<String>,
    val blueScore: Int,
    val blueAdvancement: String? = null,
    val bluePlayoffAlliance: Alliance? = null,
    val winningAlliance: String?,
    val scoreBreakdown: String? = null,
    val videos: String? = null,
)

/**
 * Helper function to returns the Advancement on each side of a Playoff Match
 * @param playoffType The type of playoff of this event
 * @return (winningAllianceAdvancement, losingAllianceAdvancement) or null
 */
private fun Match.getAdvancement(playoffType: PlayoffType?): Pair<String, String>? {
    if (this.compLevel !in PLAYOFF_COMP_LEVELS || compLevel == CompLevel.FINAL) return null

    return when (playoffType) {
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
    }
}

/**
 * Returns a new object with the Playoff Advancement for Red and Blue of this Match
 * @param playoffType The type of playoff of this event
 * @return A new Match object with updated Playoff Alliances
 */
fun Match.withAdvancement(playoffType: PlayoffType?): Match {
    val (winAdvancement, loseAdvancement) = this.getAdvancement(playoffType) ?: return this.copy(
        redAdvancement = null,
        blueAdvancement = null,
    )

    return this.copy(
        redAdvancement = if (winningAlliance == "red") winAdvancement else loseAdvancement,
        blueAdvancement = if (winningAlliance == "blue") winAdvancement else loseAdvancement,
    )
}

/**
 * Helper function to calculates which Playoff Alliance on a side in a Match
 * @param alliances All Playoff Alliances (1-8) in the given Event
 * @param teamKeys Keys for a given Alliance (Red/Blue) in a Match
 * @return The Playoff Alliance or null if not found
 */
private fun calculateAlliance(alliances: List<Alliance>, teamKeys: List<String>): Alliance? {
    // Each team cannot belong to more than one alliance, and there can only be one backup team.
    // That is, at least two teams must be "picks"

    return alliances.firstOrNull { alliance -> teamKeys.any { it in alliance.picks } }
}

/**
 * Calculates and returns a new object with the Playoff Alliances for Red and Blue of this Match
 * @param alliances All Playoff Alliances (1-8) in the given Event
 * @return A new Match object with updated Playoff Alliances
 */
fun Match.withPlayoffAlliances(alliances: List<Alliance>?): Match {
    if (compLevel !in PLAYOFF_COMP_LEVELS || alliances == null) {
        // Only Playoff matches have Alliances || We don't know what alliances exist
        return this
    }
    return this.copy(
        redPlayoffAlliance = calculateAlliance(alliances, this.redTeamKeys),
        bluePlayoffAlliance = calculateAlliance(alliances, this.blueTeamKeys),
    )
}

/**
 * Competition level
 * @param code CompLevel code in the TBA API
 * @param order Order for sorting matches. Lower is earlier in the competition.
 */
enum class CompLevel(
    val code: String,
    val order: Int,
) {
    QUAL("qm", 0),
    OCTOFINAL("ef", 1),
    QUARTERFINAL("qf", 2),
    SEMIFINAL("sf", 3),
    FINAL("f", 4),
    OTHER("", Int.MAX_VALUE);

    companion object {
        private val codeMap = entries.associateBy(CompLevel::code)
        fun fromCode(code: String) = codeMap[code] ?: OTHER
    }
}

sealed interface MatchGroup {
    abstract val label: String

    data class CompetitionLevel(
        val compLevel: CompLevel
    ) : MatchGroup {
        override val label = when(compLevel) {
            CompLevel.QUAL -> "Qualifications"
            CompLevel.OCTOFINAL -> "Eighths"
            CompLevel.QUARTERFINAL -> "Quarterfinals"
            CompLevel.SEMIFINAL -> "Semifinals"
            CompLevel.FINAL -> "Finals"
            CompLevel.OTHER -> compLevel.code
        }
    }

    sealed class DoubleEliminationRound : MatchGroup {
        object Unknown: DoubleEliminationRound() {
            override val label = "Round ?"
        }
        object Finals: DoubleEliminationRound(){
            override val label = "Finals"
        }
        data class Round(val number: Int): DoubleEliminationRound(){
            override val label = "Round $number"
        }
    }
}
