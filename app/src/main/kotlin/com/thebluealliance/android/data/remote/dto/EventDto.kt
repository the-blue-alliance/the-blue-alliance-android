package com.thebluealliance.android.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EventDto(
    val key: String,
    val name: String,
    @SerialName("event_code") val eventCode: String,
    val year: Int,
    @SerialName("short_name") val shortName: String? = null,
    @SerialName("event_type_string") val eventTypeString: String? = null,
    @SerialName("event_type") val eventType: Int? = null,
    val district: DistrictDto? = null,
    val address: String? = null,
    @SerialName("gmaps_url") val gmapsUrl: String? = null,
    @SerialName("location_name") val locationName: String? = null,
    val city: String? = null,
    @SerialName("state_prov") val stateProv: String? = null,
    val country: String? = null,
    val timezone: String? = null,
    val website: String? = null,
    val webcasts: List<WebcastDto>? = null,
    val week: Int? = null,
    @SerialName("start_date") val startDate: String? = null,
    @SerialName("end_date") val endDate: String? = null,
)

@Serializable
data class WebcastDto(
    val type: String,
    val channel: String,
    val file: String? = null,
)
