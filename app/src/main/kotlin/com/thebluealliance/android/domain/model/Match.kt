package com.thebluealliance.android.domain.model

data class Match(
    val key: String,
    val eventKey: String,
    val compLevel: String,
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

/** Short label for list rows, e.g. "Q1", "SF2-1", "F1-1" */
val Match.shortLabel: String get() = when (compLevel) {
    "qm" -> "Q$matchNumber"
    "qf" -> "QF$setNumber-$matchNumber"
    "sf" -> "SF$setNumber-$matchNumber"
    "f" -> "F$setNumber-$matchNumber"
    else -> "$compLevel$setNumber-$matchNumber"
}

/** Full label for title bars, e.g. "Qual 1", "Final 1-1" */
val Match.fullLabel: String get() = when (compLevel) {
    "qm" -> "Qual $matchNumber"
    "qf" -> "QF$setNumber-$matchNumber"
    "sf" -> "SF$setNumber-$matchNumber"
    "f" -> "Final $setNumber-$matchNumber"
    else -> "$compLevel$setNumber-$matchNumber"
}
