package com.thebluealliance.android.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "event_coprs")
data class EventCOPRsEntity(
    @PrimaryKey val eventKey: String,
    val coprs: String, // JSON map of stat name -> team stats
)

