package com.thebluealliance.android.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AwardDto(
    val name: String,
    @SerialName("award_type") val awardType: Int,
    @SerialName("event_key") val eventKey: String,
    val year: Int,
    @SerialName("recipient_list") val recipientList: List<AwardRecipientDto> = emptyList(),
)

@Serializable
data class AwardRecipientDto(
    val awardee: String? = null,
    @SerialName("team_key") val teamKey: String? = null,
)
