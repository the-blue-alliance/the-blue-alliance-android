package com.thebluealliance.android.domain.model

data class Award(
    val eventKey: String,
    val awardType: Int,
    val teamKey: String,
    val name: String,
    val year: Int,
)
