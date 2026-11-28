package com.thebluealliance.android.domain

/** Separates independent pieces of info on one line, e.g. "Pit 12 · San Jose, CA". */
const val INFO_SEPARATOR = " · "

/** "254 · The Cheesy Poofs"; the name stands in for a missing nickname, and with neither it is just "254". */
fun teamLabel(
    number: Int,
    nickname: String?,
    name: String? = null,
): String = teamLabel(number.toString(), nickname, name)

/**
 * "254 · The Cheesy Poofs"; the name stands in for a missing nickname, and with neither it is just
 * "254". Surrounding whitespace is trimmed and blank parts are dropped with their separator.
 */
fun teamLabel(
    number: String,
    nickname: String?,
    name: String? = null,
): String {
    val label = nickname?.trim()?.ifEmpty { null } ?: name?.trim()?.ifEmpty { null }
    return listOfNotNull(number.trim().ifEmpty { null }, label).joinToString(INFO_SEPARATOR)
}
