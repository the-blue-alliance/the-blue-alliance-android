package com.thebluealliance.android.data.local.entity

import androidx.room.Entity

@Entity(tableName = "event_district_points", primaryKeys = ["eventKey", "teamKey", "source"])
data class EventDistrictPointsEntity(
    val eventKey: String,
    val teamKey: String,
    val source: String,
    val qualPoints: Int,
    val elimPoints: Int,
    val alliancePoints: Int,
    val awardPoints: Int,
    val rookieBonus: Int,
    val total: Int,
)
