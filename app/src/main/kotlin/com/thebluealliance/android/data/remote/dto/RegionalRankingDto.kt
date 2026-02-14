package com.thebluealliance.android.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RegionalRankingDto(
    @SerialName("team_key") val teamKey: String,
    val rank: Int,
    @SerialName("point_total") val pointTotal: Int,
    @SerialName("rookie_bonus") val rookieBonus: Int? = null,
    @SerialName("single_event_bonus") val singleEventBonus: Int? = null,
    @SerialName("event_points") val eventPoints: List<RegionalEventPointsDto>? = null,
)

@Serializable
data class RegionalEventPointsDto(
    @SerialName("event_key") val eventKey: String,
    @SerialName("alliance_points") val alliancePoints: Int,
    @SerialName("award_points") val awardPoints: Int,
    @SerialName("qual_points") val qualPoints: Int,
    @SerialName("elim_points") val elimPoints: Int,
    val total: Int,
)
