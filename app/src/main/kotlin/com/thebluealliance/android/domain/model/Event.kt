package com.thebluealliance.android.domain.model

data class Event(
    val key: String,
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
    val locationName: String?,
    val address: String?,
    val gmapsUrl: String?,
    val webcasts: List<Webcast>,
)

data class Webcast(
    val type: String,
    val channel: String,
    val file: String?,
)
