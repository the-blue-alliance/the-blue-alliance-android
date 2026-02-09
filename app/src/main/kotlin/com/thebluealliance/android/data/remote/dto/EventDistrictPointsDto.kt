package com.thebluealliance.android.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EventDistrictPointsResponseDto(
    val points: Map<String, EventDistrictPointsEntryDto> = emptyMap(),
)

@Serializable
data class EventDistrictPointsEntryDto(
    @SerialName("qual_points") val qualPoints: Int = 0,
    @SerialName("elim_points") val elimPoints: Int = 0,
    @SerialName("alliance_points") val alliancePoints: Int = 0,
    @SerialName("award_points") val awardPoints: Int = 0,
    val total: Int = 0,
)
