package com.thebluealliance.android.util

private const val TBA_EVENT_BASE_URL = "https://www.thebluealliance.com/event"
private const val NEXUS_EVENT_BASE_URL = "https://frc.nexus/en/event"

fun buildTbaPitMapUrl(
    eventKey: String,
    highlightedTeamKeys: List<String> = emptyList(),
): String {
    val normalizedKeys =
        highlightedTeamKeys
            .map { it.trim() }
            .filter { it.startsWith("frc") }
            .distinct()
    val base = "$TBA_EVENT_BASE_URL/$eventKey/pitmap"
    if (normalizedKeys.isEmpty()) return base
    return "$base?teams=${normalizedKeys.joinToString(",")}"
}

fun buildNexusPitMapUrl(
    eventKey: String,
    highlightedTeamKey: String? = null,
): String {
    val teamNumber = highlightedTeamKey?.teamNumber?.takeIf { it.isNotBlank() }
    return if (teamNumber == null) {
        "$NEXUS_EVENT_BASE_URL/$eventKey/map"
    } else {
        "$NEXUS_EVENT_BASE_URL/$eventKey/team/$teamNumber/map"
    }
}

/**
 * Builds the Nexus event code (e.g. `"2026MIKET"`) from TBA event data.
 *
 * For most events the code is just the year prepended to the event-key suffix. Championship
 * divisions have a different FIRST API code (e.g. TBA key `2026cmptxcur` uses FIRST code `CUR`),
 * which the API returns as [firstEventCode] and must be preferred when present.
 */
fun buildNexusEventCode(
    eventKey: String,
    year: Int,
    firstEventCode: String?,
): String =
    if (!firstEventCode.isNullOrBlank()) {
        "$year${firstEventCode.uppercase()}"
    } else {
        "$year${eventKey.drop(4).uppercase()}"
    }
