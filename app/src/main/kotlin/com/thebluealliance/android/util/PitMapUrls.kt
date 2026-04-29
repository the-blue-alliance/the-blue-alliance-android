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
    val teamNumber = highlightedTeamKey?.removePrefix("frc")?.takeIf { it.isNotBlank() }
    return if (teamNumber == null) {
        "$NEXUS_EVENT_BASE_URL/$eventKey/map"
    } else {
        "$NEXUS_EVENT_BASE_URL/$eventKey/team/$teamNumber/map"
    }
}

