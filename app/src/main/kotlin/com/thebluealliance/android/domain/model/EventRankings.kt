package com.thebluealliance.android.domain.model

data class EventRankings(
    val rankings: List<Ranking>,
    val sortOrderInfo: List<RankingSortOrder>,
    val extraStatsInfo: List<RankingSortOrder>,
)
