package com.thebluealliance.android.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "event_insights")
data class EventInsightsEntity(
    @PrimaryKey val eventKey: String,
    val qualInsights: String?, // JSON object of qual insights
    val playoffInsights: String?, // JSON object of playoff insights
)

