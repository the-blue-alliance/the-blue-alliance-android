package com.thebluealliance.android.domain.model

data class Ranking(
    val eventKey: String,
    val teamKey: String,
    val rank: Int,
    val dq: Int,
    val matchesPlayed: Int,
    val wins: Int,
    val losses: Int,
    val ties: Int,
    val qualAverage: Double?,
    val sortOrders: List<Double> = emptyList(),
    val extraStats: List<Double> = emptyList(),
)

/** Win-loss-tie record formatted as "W-L-T", e.g. "10-2-0". */
val Ranking.recordString: String
    get() = "$wins-$losses-$ties"
