package com.thebluealliance.android.data.local.entity

import androidx.room.Entity

@Entity(tableName = "event_district_points", primaryKeys = ["eventKey", "teamKey"])
data class EventDistrictPointsEntity(
    val eventKey: String,
    val teamKey: String,
    val qualPoints: Int,
    val elimPoints: Int,
    val alliancePoints: Int,
    val awardPoints: Int,
    val total: Int,
)
