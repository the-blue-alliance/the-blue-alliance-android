package com.thebluealliance.android.domain.model

data class Media(
    val teamKey: String,
    val type: String,
    val foreignKey: String,
    val year: Int,
    val preferred: Boolean,
    val details: String?,
    val base64Image: String? = null,
) {
    val isAvatar: Boolean = type == "avatar" && base64Image != null
}
