package com.thebluealliance.android.data.local.entity

import androidx.room.Entity

@Entity(tableName = "favorites", primaryKeys = ["modelKey", "modelType"])
data class FavoriteEntity(
    val modelKey: String,
    val modelType: Int,
)
