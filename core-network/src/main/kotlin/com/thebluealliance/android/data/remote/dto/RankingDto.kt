package com.thebluealliance.android.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RankingResponseDto(
    val rankings: List<RankingItemDto> = emptyList(),
    // Nullable ELEMENTS (not just a nullable list): a null entry is kept in place so it stays
    // index-aligned with the sort_orders column it labels. Dropping would shift columns. The
    // mapper coalesces a null to a blank-name placeholder, so the domain/UI type is unaffected.
    @SerialName("sort_order_info") val sortOrderInfo: List<RankingSortOrderDto?>? = null,
    @SerialName("extra_stats_info") val extraStatsInfo: List<RankingSortOrderDto?>? = null,
)

@Serializable
data class RankingItemDto(
    @SerialName("team_key") val teamKey: String,
    val rank: Int,
    val dq: Int = 0,
    @SerialName("matches_played") val matchesPlayed: Int = 0,
    val record: TeamRecordDto? = null,
    // Nullable ELEMENTS: a null component is kept in place (index-aligned with sort_order_info)
    // rather than dropped, so the UI renders that one cell blank instead of shifting every later
    // tiebreaker under the wrong column header. A team with incomplete ranking data is the
    // realistic source; JSON null round-trips through the Room entity cleanly (NaN would not). #1445.
    @SerialName("sort_orders") val sortOrders: List<Double?> = emptyList(),
    @SerialName("extra_stats") val extraStats: List<Double?> = emptyList(),
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
