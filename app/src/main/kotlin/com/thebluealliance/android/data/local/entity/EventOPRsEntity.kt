package com.thebluealliance.android.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "event_oprs")
data class EventOPRsEntity(
    @PrimaryKey val eventKey: String,
    val oprs: String, // JSON
    val dprs: String, // JSON
    val ccwms: String, // JSON
)
