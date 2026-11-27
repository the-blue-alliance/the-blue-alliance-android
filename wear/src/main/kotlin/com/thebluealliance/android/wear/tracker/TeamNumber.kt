package com.thebluealliance.android.wear.tracker

/** Turns what the user typed into a team number the API knows, or null if it can't be one. */
internal object TeamNumber {
    fun normalize(raw: String): String? = raw.filter { it.isDigit() }.ifBlank { null }
}
