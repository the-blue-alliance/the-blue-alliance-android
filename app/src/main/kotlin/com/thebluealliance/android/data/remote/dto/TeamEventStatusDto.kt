package com.thebluealliance.android.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TeamEventStatusDto(
    @SerialName("pit_location") val pitLocation: String? = null,
)
