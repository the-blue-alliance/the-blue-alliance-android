package com.thebluealliance.android.data.remote.dto

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class EventOPRsDto(
    // Use JsonObject to capture all fields dynamically
    // This allows us to get COPRs without knowing the stat names ahead of time
    val oprs: Map<String, Double> = emptyMap(),
    val dprs: Map<String, Double> = emptyMap(),
    val ccwms: Map<String, Double> = emptyMap(),
)

// Alternative: Use JsonElement for the whole response
@Serializable
data class EventOPRsResponseDto(
    val response: JsonObject
)

