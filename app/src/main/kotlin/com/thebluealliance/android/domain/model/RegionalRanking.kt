package com.thebluealliance.android.domain.model

data class RegionalRanking(
    val year: Int,
    val teamKey: String,
    val rank: Int,
    val pointTotal: Int,
    val rookieBonus: Int,
    val singleEventBonus: Int,
)
