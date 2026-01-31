package com.thebluealliance.android.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class MediaDto(
    val type: String,
    @SerialName("foreign_key") val foreignKey: String,
    val preferred: Boolean = false,
    val details: JsonObject? = null,
    @SerialName("base64Image") val base64Image: String? = null,
)
