package com.thebluealliance.android.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DistrictDto(
    val abbreviation: String,
    @SerialName("display_name") val displayName: String,
    val key: String,
    val year: Int,
)
