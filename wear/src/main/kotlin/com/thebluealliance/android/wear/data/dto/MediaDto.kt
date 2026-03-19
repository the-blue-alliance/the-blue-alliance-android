package com.thebluealliance.android.wear.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive

@Serializable
data class MediaDto(
    val type: String,
    @SerialName("foreign_key") val foreignKey: String,
    val details: JsonObject? = null,
) {
    val base64Image: String?
        get() = details?.get("base64Image")?.jsonPrimitive?.content
}
