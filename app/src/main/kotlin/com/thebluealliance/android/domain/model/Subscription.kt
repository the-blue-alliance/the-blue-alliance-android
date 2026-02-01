package com.thebluealliance.android.domain.model

data class Subscription(
    val modelKey: String,
    val modelType: Int,
    val notifications: List<String>,
)
