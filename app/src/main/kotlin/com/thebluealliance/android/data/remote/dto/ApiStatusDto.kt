package com.thebluealliance.android.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApiStatusDto(
    @SerialName("current_season") val currentSeason: Int,
    @SerialName("max_season") val maxSeason: Int,
)
