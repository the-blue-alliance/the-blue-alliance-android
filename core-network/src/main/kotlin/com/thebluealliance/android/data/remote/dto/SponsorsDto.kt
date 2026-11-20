package com.thebluealliance.android.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** `GET /api/v3/sponsors`: the platinum sponsors to credit, in display order. */
@Serializable
data class SponsorsResponseDto(
    val sponsors: List<SponsorDto> = emptyList(),
)

@Serializable
data class SponsorDto(
    val name: String,
    val url: String,
    /** "wordmark" or "mark"; anything else is drawn as a wordmark. */
    @SerialName("logo_style") val logoStyle: String? = null,
    /** Full-color logo for light backgrounds. */
    @SerialName("logo_url") val logoUrl: String? = null,
    /** White logo for dark backgrounds. */
    @SerialName("logo_url_dark") val logoUrlDark: String? = null,
)
