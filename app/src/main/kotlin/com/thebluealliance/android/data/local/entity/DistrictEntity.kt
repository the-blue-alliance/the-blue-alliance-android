package com.thebluealliance.android.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "districts", indices = [Index("year")])
data class DistrictEntity(
    @PrimaryKey val key: String,
    val abbreviation: String,
    val displayName: String,
    val year: Int,
)
