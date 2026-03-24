package com.thebluealliance.android.wear.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TeamDto(
    val key: String,
    val nickname: String? = null,
    @SerialName("team_number") val teamNumber: Int,
)
