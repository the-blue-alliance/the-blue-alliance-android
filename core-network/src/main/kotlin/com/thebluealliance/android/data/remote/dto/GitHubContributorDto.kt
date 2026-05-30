package com.thebluealliance.android.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GitHubContributorDto(
    val login: String,
    @SerialName("avatar_url") val avatarUrl: String,
    val contributions: Int,
    @SerialName("html_url") val htmlUrl: String,
)
