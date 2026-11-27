package com.thebluealliance.android.wear.tracker

/** Turns what the user typed into a team number the API knows, or null if it can't be one. */
internal object TeamNumber {
    /** FRC team numbers are 1 to 5 digits with no leading zeros ("frc018" doesn't exist). */
    private const val MAX_DIGITS = 5

    fun normalize(raw: String): String? =
        raw
            .filter { it.isDigit() }
            .trimStart('0')
            .takeIf { it.isNotEmpty() && it.length <= MAX_DIGITS }
}
