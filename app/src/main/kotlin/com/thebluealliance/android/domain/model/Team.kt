package com.thebluealliance.android.domain.model

data class Team(
    val key: String,
    val number: Int,
    val name: String?,
    val nickname: String?,
    val city: String?,
    val state: String?,
    val country: String?,
    val rookieYear: Int?,
)
