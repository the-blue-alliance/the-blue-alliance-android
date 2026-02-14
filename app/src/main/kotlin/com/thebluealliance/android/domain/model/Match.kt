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

enum class CompLevel(val code: String) {
    QUAL("qm"),
    OCTOFINAL("ef"),
    QUARTERFINAL("qf"),
    SEMIFINAL("sf"),
    FINAL("f"),
    PRACTICE("pr"),
    OTHER("");

    companion object {
        private val codeMap = entries.associateBy(CompLevel::code)
        fun fromCode(code: String) = codeMap[code] ?: OTHER
    }
}
