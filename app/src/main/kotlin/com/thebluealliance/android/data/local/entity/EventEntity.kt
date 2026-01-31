package com.thebluealliance.android.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "events")
data class EventEntity(
    @PrimaryKey val key: String,
    val name: String,
    val eventCode: String,
    val year: Int,
    val type: Int?,
    val district: String?,
    val city: String?,
    val state: String?,
    val country: String?,
    val startDate: String?,
    val endDate: String?,
    val week: Int?,
    val shortName: String?,
    val website: String?,
    val timezone: String?,
    val webcasts: String?,
    val locationName: String?,
    val address: String?,
    val gmapsUrl: String?,
)
