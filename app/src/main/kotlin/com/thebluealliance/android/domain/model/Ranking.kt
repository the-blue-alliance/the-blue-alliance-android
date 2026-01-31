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
)
