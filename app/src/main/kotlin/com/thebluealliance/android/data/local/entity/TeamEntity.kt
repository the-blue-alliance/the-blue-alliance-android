package com.thebluealliance.android.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "teams")
data class TeamEntity(
    @PrimaryKey val key: String,
    val number: Int,
    val name: String?,
    val nickname: String?,
    val city: String?,
    val state: String?,
    val country: String?,
    val rookieYear: Int?,
)
