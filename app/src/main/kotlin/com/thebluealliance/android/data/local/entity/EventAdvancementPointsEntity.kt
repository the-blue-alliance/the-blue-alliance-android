package com.thebluealliance.android.data.local.entity

import androidx.room.Entity

@Entity(tableName = "event_advancement_points", primaryKeys = ["eventKey", "teamKey", "source"])
data class EventAdvancementPointsEntity(
    val eventKey: String,
    val teamKey: String,
    val source: PointsSource,
    val qualPoints: Int,
    val elimPoints: Int,
    val alliancePoints: Int,
    val awardPoints: Int,
    val rookieBonus: Int,
    val total: Int,
)

/** Discriminates the two distinct TBA datasets that share this table. */
enum class PointsSource {
    DISTRICT,
    REGIONAL,
}
