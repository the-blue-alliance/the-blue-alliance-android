package com.thebluealliance.android.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RankingResponseDto(
    val rankings: List<RankingItemDto> = emptyList(),
    @SerialName("sort_order_info") val sortOrderInfo: List<RankingSortOrderDto>? = null,
    @SerialName("extra_stats_info") val extraStatsInfo: List<RankingSortOrderDto>? = null,
)

@Serializable
data class RankingItemDto(
    @SerialName("team_key") val teamKey: String,
    val rank: Int,
    val dq: Int = 0,
    @SerialName("matches_played") val matchesPlayed: Int = 0,
    val record: TeamRecordDto? = null,
    @SerialName("sort_orders") val sortOrders: List<Double> = emptyList(),
    @SerialName("extra_stats") val extraStats: List<Double> = emptyList(),
    @SerialName("qual_average") val qualAverage: Double? = null,
)

@Serializable
data class RankingSortOrderDto(
    val name: String,
    val precision: Int,
)

@Serializable
data class TeamRecordDto(
    val wins: Int = 0,
    val losses: Int = 0,
    val ties: Int = 0,
)
