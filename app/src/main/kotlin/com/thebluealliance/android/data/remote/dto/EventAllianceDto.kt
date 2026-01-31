package com.thebluealliance.android.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class EventAllianceDto(
    val picks: List<String> = emptyList(),
    val name: String? = null,
    val declines: List<String> = emptyList(),
    val backup: AllianceBackupDto? = null,
    val status: EventAllianceStatusDto? = null,
)

@Serializable
data class AllianceBackupDto(
    @Suppress("PropertyName") val `in`: String? = null,
    val out: String? = null,
)

@Serializable
data class EventAllianceStatusDto(
    val status: String? = null,
)
