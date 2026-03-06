package com.thebluealliance.android.data.remote.dto

// EventInsights is returned as a plain JSON object with "qual" and "playoff" keys
// We store the raw JSON strings
data class EventInsightsDto(
    val qual: String? = null,
    val playoff: String? = null,
)




