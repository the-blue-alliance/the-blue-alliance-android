package com.thebluealliance.android.domain.model

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
    val redPlayoffAlliance: Alliance? = null,
    val blueTeamKeys: List<String>,
    val blueScore: Int,
    val bluePlayoffAlliance: Alliance? = null,
    val winningAlliance: String?,
    val scoreBreakdown: String? = null,
    val videos: String? = null,
)

/**
 * Helper function to calculates which Playoff Alliance on a side in a Match
 * @param alliances All Playoff Alliances (1-8) in the given Event
 * @param teamKeys Keys for a given Alliance (Red/Blue) in a Match
 * @return The Playoff Alliance number or null if not found
 */
private fun calculateAlliance(alliances: List<Alliance>, teamKeys: List<String>): Alliance? {
    // Each team cannot belong to more than one alliance, and there can only be one backup team.
    // That is, at least two teams must be "picks"

    for (alliance in alliances) {
        if (teamKeys.any { it in alliance.picks }) {
            return alliance
        }
    }
    return null
}

/**
 * Calculates and returns a new object with the Playoff Alliances for Red and Blue of this Match
 * @param alliances All Playoff Alliances (1-8) in the given Event
 * @return A new Match object with updated Playoff Alliances
 */
fun Match.calculatePlayoffAlliances(alliances: List<Alliance>?): Match {
    if (!compLevel.isPlayoff || alliances == null) {
        // Only Playoff matches have Alliances
        return this.copy()
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
    val isPlayoff: Boolean,
    val order: Int,
) {
    QUAL("qm", false, 0),
    OCTOFINAL("ef", true, 1),
    QUARTERFINAL("qf", true, 2),
    SEMIFINAL("sf", true, 3),
    FINAL("f", true, 4),
    OTHER("", false, Int.MAX_VALUE);

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
