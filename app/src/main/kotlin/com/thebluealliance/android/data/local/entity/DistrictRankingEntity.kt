package com.thebluealliance.android.data.local.entity

import androidx.room.Entity

@Entity(tableName = "district_rankings", primaryKeys = ["districtKey", "teamKey"])
data class DistrictRankingEntity(
    val districtKey: String,
    val teamKey: String,
    val rank: Int,
    val pointTotal: Double,
    val rookieBonus: Double,
    val eventPoints: String,
)
