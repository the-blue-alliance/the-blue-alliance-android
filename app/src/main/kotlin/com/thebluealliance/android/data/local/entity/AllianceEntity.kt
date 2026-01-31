package com.thebluealliance.android.data.local.entity

import androidx.room.Entity

@Entity(tableName = "alliances", primaryKeys = ["eventKey", "number"])
data class AllianceEntity(
    val eventKey: String,
    val number: Int,
    val name: String?,
    val picks: String,
    val declines: String,
    val backupIn: String?,
    val backupOut: String?,
)
