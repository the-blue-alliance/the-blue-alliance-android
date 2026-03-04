package com.thebluealliance.android.domain.model

data class DistrictRanking(
    val districtKey: String,
    val teamKey: String,
    val rank: Int,
    val pointTotal: Double,
    val rookieBonus: Double,
)

data class RegionalRanking(
    val year: Int,
    val teamKey: String,
    val rank: Int,
    val pointTotal: Double,
    val rookieBonus: Double,
    val singleEventBonus: Double,
    val eventPoints: List<RegionalEventPoints>,
)

data class RegionalEventPoints(
    val eventKey: String,
    val total: Double,
)
