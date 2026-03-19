package com.thebluealliance.android.wear.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EventDto(
    val key: String,
    val name: String,
    @SerialName("short_name") val shortName: String? = null,
    @SerialName("event_type") val eventType: Int? = null,
    @SerialName("start_date") val startDate: String? = null,
    @SerialName("end_date") val endDate: String? = null,
    @SerialName("playoff_type") val playoffType: Int? = null,
)
