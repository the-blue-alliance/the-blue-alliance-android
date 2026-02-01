package com.thebluealliance.android.domain.model

data class Match(
    val key: String,
    val eventKey: String,
    val compLevel: String,
    val matchNumber: Int,
    val setNumber: Int,
    val time: Long?,
    val actualTime: Long?,
    val redTeamKeys: List<String>,
    val redScore: Int,
    val blueTeamKeys: List<String>,
    val blueScore: Int,
    val winningAlliance: String?,
    val scoreBreakdown: String? = null,
    val videos: String? = null,
)
