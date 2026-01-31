package com.thebluealliance.android.domain.model

data class DistrictRanking(
    val districtKey: String,
    val teamKey: String,
    val rank: Int,
    val pointTotal: Double,
    val rookieBonus: Double,
)
