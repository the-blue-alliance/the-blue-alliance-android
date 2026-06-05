package com.thebluealliance.android.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TeamDto(
    val key: String,
    val name: String? = null,
    @SerialName("team_number") val teamNumber: Int,
    val nickname: String? = null,
    val website: String? = null,
    val address: String? = null,
    @SerialName("gmaps_url") val gmapsUrl: String? = null,
    @SerialName("location_name") val locationName: String? = null,
    val city: String? = null,
    @SerialName("state_prov") val stateProv: String? = null,
    val country: String? = null,
    @SerialName("rookie_year") val rookieYear: Int? = null,
    val motto: String? = null,
)
