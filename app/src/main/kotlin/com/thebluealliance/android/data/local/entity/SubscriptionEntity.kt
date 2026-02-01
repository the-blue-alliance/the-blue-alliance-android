package com.thebluealliance.android.data.local.entity

import androidx.room.Entity

@Entity(tableName = "subscriptions", primaryKeys = ["modelKey", "modelType"])
data class SubscriptionEntity(
    val modelKey: String,
    val modelType: Int,
    val notifications: String,
)
