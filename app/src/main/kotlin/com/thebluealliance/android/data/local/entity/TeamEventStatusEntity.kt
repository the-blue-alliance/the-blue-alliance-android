package com.thebluealliance.android.data.local.entity

import androidx.room.Entity

@Entity(tableName = "team_event_status", primaryKeys = ["teamKey", "eventKey"])
data class TeamEventStatusEntity(
    val teamKey: String,
    val eventKey: String,
    val pitLocation: String?,
)
