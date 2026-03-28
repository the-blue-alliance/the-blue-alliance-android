package com.thebluealliance.android.domain.model

data class EventAdvancementPoints(
    val teamKey: String,
    val qualPoints: Int,
    val elimPoints: Int,
    val alliancePoints: Int,
    val awardPoints: Int,
    val total: Int,
)
