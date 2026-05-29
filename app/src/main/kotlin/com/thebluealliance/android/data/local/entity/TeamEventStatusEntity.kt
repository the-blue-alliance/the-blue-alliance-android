package com.thebluealliance.android.data.local.entity

import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "team_event_status",
    primaryKeys = ["teamKey", "eventKey"],
    indices = [Index("eventKey")],
)
data class TeamEventStatusEntity(
    val teamKey: String,
    val eventKey: String,
    val pitLocation: String?,
)
