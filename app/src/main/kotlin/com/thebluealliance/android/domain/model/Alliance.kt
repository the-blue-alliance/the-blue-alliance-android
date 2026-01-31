package com.thebluealliance.android.domain.model

data class Alliance(
    val eventKey: String,
    val number: Int,
    val name: String?,
    val picks: List<String>,
    val declines: List<String>,
    val backupIn: String?,
    val backupOut: String?,
)
