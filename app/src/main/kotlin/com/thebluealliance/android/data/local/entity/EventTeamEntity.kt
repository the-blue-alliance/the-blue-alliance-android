package com.thebluealliance.android.data.local.entity

import androidx.room.Entity

@Entity(tableName = "event_teams", primaryKeys = ["eventKey", "teamKey"])
data class EventTeamEntity(
    val eventKey: String,
    val teamKey: String,
)
