package com.thebluealliance.android.ui.common

/**
 * The season a year picker opens on: the newest of [available] that isn't past [currentSeason].
 *
 * TBA's max season runs ahead of the current one once next season's events are posted, and
 * opening there shows an empty screen. Falls back to the earliest season when all are later.
 */
fun defaultSeason(
    available: List<Int>,
    currentSeason: Int,
): Int? = available.filter { it <= currentSeason }.maxOrNull() ?: available.minOrNull()

/** The newest of [available] before [selected], for stepping back from an empty season. */
fun previousSeason(
    available: List<Int>,
    selected: Int,
): Int? = available.filter { it < selected }.maxOrNull()
