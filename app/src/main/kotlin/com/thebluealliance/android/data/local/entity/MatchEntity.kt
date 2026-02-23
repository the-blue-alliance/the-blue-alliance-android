package com.thebluealliance.android.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "matches")
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
    val redScore: Int,
    val blueTeamKeys: String,
    val blueScore: Int,
    val winningAlliance: String?,
    val scoreBreakdown: String?,
    val videos: String?,
)
