package com.thebluealliance.android.domain.model

data class Match(
    val key: String,
    val eventKey: String,
    val compLevel: CompLevel,
    val matchNumber: Int,
    val setNumber: Int,
    val time: Long?,
    val actualTime: Long?,
    val redTeamKeys: List<String>,
    val redScore: Int,
    val blueTeamKeys: List<String>,
    val blueScore: Int,
    val winningAlliance: String?,
    val scoreBreakdown: String? = null,
    val videos: String? = null,
)

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
