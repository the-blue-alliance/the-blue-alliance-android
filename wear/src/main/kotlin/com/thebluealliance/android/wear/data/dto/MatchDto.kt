package com.thebluealliance.android.wear.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MatchDto(
    val key: String,
    @SerialName("comp_level") val compLevel: String,
    @SerialName("match_number") val matchNumber: Int,
    @SerialName("set_number") val setNumber: Int,
    val alliances: MatchAlliancesDto? = null,
    val time: Long? = null,
    @SerialName("predicted_time") val predictedTime: Long? = null,
    @SerialName("winning_alliance") val winningAlliance: String? = null,
)

@Serializable
data class MatchAlliancesDto(
    val red: MatchAllianceDto? = null,
    val blue: MatchAllianceDto? = null,
)

@Serializable
data class MatchAllianceDto(
    val score: Int = -1,
    @SerialName("team_keys") val teamKeys: List<String> = emptyList(),
)
