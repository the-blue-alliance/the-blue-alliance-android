package com.thebluealliance.android.data.local.entity

import androidx.room.Entity

@Entity(tableName = "media", primaryKeys = ["teamKey", "type", "foreignKey"])
data class MediaEntity(
    val teamKey: String,
    val type: String,
    val foreignKey: String,
    val year: Int,
    val preferred: Boolean,
    val details: String?,
    val base64Image: String? = null,
)
