package com.thebluealliance.android.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class EventStatsResponseDto(
    val oprs: Map<String, Double> = emptyMap(),
    val dprs: Map<String, Double> = emptyMap(),
    val ccwms: Map<String, Double> = emptyMap(),
)

