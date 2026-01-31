package com.thebluealliance.android.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class MatchDto(
    val key: String,
    @SerialName("event_key") val eventKey: String,
    @SerialName("comp_level") val compLevel: String,
    @SerialName("match_number") val matchNumber: Int,
    @SerialName("set_number") val setNumber: Int,
    val alliances: MatchAlliancesDto? = null,
    @SerialName("score_breakdown") val scoreBreakdown: JsonObject? = null,
    val videos: List<MatchVideoDto>? = null,
    val time: Long? = null,
    @SerialName("actual_time") val actualTime: Long? = null,
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
    @SerialName("surrogate_team_keys") val surrogateTeamKeys: List<String> = emptyList(),
)

@Serializable
data class MatchVideoDto(
    val type: String,
    val key: String,
)
