package com.thebluealliance.android.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "matches", indices = [Index("eventKey")])
data class MatchEntity(
    @PrimaryKey val key: String,
    val eventKey: String,
    val compLevel: String,
    val matchNumber: Int,
    val setNumber: Int,
    val time: Long?,
    val actualTime: Long?,
    val predictedTime: Long?,
    val redTeamKeys: String,
    val redSurrogateTeamKeys: String,
    val redScore: Int,
    val blueTeamKeys: String,
    val blueSurrogateTeamKeys: String,
    val blueScore: Int,
    val winningAlliance: String?,
    val scoreBreakdown: String?,
    val videos: String?,
)
