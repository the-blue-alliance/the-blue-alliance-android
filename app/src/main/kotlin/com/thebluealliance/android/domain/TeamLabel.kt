package com.thebluealliance.android.domain

/** The team number with its nickname (or name), e.g. for a team row title. */
fun teamLabel(
    number: Int,
    nickname: String?,
    name: String? = null,
): String = teamLabel(number.toString(), nickname, name)

/** The team number with its nickname (or name), e.g. for a team row title. */
fun teamLabel(
    number: String,
    nickname: String?,
    name: String? = null,
): String = "$number - ${nickname ?: name ?: ""}"
