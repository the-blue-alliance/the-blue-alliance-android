package com.thebluealliance.android.domain.model

/** A platinum sponsor credited on the More tab. Logo models are anything Coil loads (URL, resource id). */
data class PlatinumSponsor(
    val name: String,
    val url: String,
    val logoOnLight: Any?,
    val logoOnDark: Any?,
    val logoStyle: SponsorLogoStyle = SponsorLogoStyle.Wordmark,
)

/** A wordmark already spells the name; a square mark is drawn with the name beside it. */
enum class SponsorLogoStyle { Wordmark, Mark }
