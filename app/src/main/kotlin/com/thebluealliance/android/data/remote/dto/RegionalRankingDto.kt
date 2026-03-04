package com.thebluealliance.android.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RegionalRankingDto(
    @SerialName("team_key") val teamKey: String,
    val rank: Int,
    @SerialName("rookie_bonus") val rookieBonus: Double = 0.0,
    @SerialName("single_event_bonus") val singleEventBonus: Double = 0.0,
    @SerialName("point_total") val pointTotal: Double,
    @SerialName("event_points") val eventPoints: List<RegionalEventPointsDto> = emptyList(),
)

@Serializable
data class RegionalEventPointsDto(
    @SerialName("event_key") val eventKey: String,
    @SerialName("alliance_points") val alliancePoints: Double = 0.0,
    @SerialName("award_points") val awardPoints: Double = 0.0,
    @SerialName("qual_points") val qualPoints: Double = 0.0,
    @SerialName("elim_points") val elimPoints: Double = 0.0,
    val total: Double = 0.0,
)

