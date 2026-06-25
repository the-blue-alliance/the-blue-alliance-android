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
    // Nullable elements: a null is a missing component kept in place so it stays index-aligned
    // with sortOrderInfo (the UI renders that cell blank). #1445.
    val sortOrders: List<Double?> = emptyList(),
    val extraStats: List<Double?> = emptyList(),
)
