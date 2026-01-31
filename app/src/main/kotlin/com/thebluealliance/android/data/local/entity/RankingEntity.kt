package com.thebluealliance.android.data.local.entity

import androidx.room.Entity

@Entity(tableName = "rankings", primaryKeys = ["eventKey", "teamKey"])
data class RankingEntity(
    val eventKey: String,
    val teamKey: String,
    val rank: Int,
    val dq: Int,
    val matchesPlayed: Int,
    val wins: Int,
    val losses: Int,
    val ties: Int,
    val sortOrders: String,
    val qualAverage: Double?,
)
