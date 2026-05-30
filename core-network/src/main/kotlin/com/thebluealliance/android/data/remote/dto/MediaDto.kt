package com.thebluealliance.android.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive

@Serializable
data class MediaDto(
    val type: String,
    @SerialName("foreign_key") val foreignKey: String,
    val preferred: Boolean = false,
    val details: JsonObject? = null,
    @SerialName("base64Image") private val rawBase64Image: String? = null,
) {
    /**
     * Avatar image data. TBA returns it top-level on some payloads and nested under
     * [details] on others; expose one flat accessor so callers don't reach into raw JSON.
     */
    val base64Image: String?
        get() = rawBase64Image ?: details?.get("base64Image")?.jsonPrimitive?.content
}
