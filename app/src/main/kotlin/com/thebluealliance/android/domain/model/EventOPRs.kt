package com.thebluealliance.android.domain.model

data class EventOPRs(
    val oprs: Map<String, Double> = emptyMap(),
    val dprs: Map<String, Double> = emptyMap(),
    val ccwms: Map<String, Double> = emptyMap(),
)
