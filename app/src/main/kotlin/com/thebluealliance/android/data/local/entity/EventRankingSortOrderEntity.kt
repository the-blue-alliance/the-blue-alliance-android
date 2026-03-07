package com.thebluealliance.android.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "event_ranking_sort_orders")
data class EventRankingSortOrderEntity(
    @PrimaryKey
    val eventKey: String,
    val sortOrderInfo: String, // JSON array of RankingSortOrderDto
    val extraStatsInfo: String? = null, // JSON array of RankingSortOrderDto
)

