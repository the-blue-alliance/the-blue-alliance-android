package com.thebluealliance.android.data.local.entity

import androidx.room.Entity

@Entity(tableName = "regional_rankings", primaryKeys = ["year", "teamKey"])
data class RegionalRankingEntity(
    val year: Int,
    val teamKey: String,
    val rank: Int,
    val pointTotal: Int,
    val rookieBonus: Int,
    val singleEventBonus: Int,
    val eventPoints: String,
)
