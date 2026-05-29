package com.thebluealliance.android.data.local.entity

import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "event_teams",
    primaryKeys = ["eventKey", "teamKey"],
    indices = [Index("teamKey")],
)
data class EventTeamEntity(
    val eventKey: String,
    val teamKey: String,
)
