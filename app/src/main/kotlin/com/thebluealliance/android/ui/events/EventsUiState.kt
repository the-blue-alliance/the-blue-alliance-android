package com.thebluealliance.android.ui.events

import com.thebluealliance.android.domain.model.Event
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.ChronoUnit

data class EventSection(val label: String, val events: List<Event>)

sealed interface EventsUiState {
    data object Loading : EventsUiState
    data class Success(
        val sections: List<EventSection>,
        val favoriteEventKeys: Set<String> = emptySet(),
    ) : EventsUiState
    data class Error(val message: String) : EventsUiState
}

data class ThisWeekResult(val label: String, val events: List<Event>)

/**
 * Computes "This Week" events using a hybrid approach:
 * - During regular season: uses the `week` field to find the current competition week
 * - During offseason/preseason: falls back to calendar-week overlap (Monday-Sunday)
 *
 * Returns null if no events match or if viewing a non-current year.
 */
fun computeThisWeekEvents(
    allEvents: List<Event>,
    today: LocalDate,
    selectedYear: Int,
): ThisWeekResult? {
    if (selectedYear != today.year) return null

    val currentWeek = findCurrentCompetitionWeek(allEvents, today)

    val label: String
    val events: List<Event>

    if (currentWeek != null) {
        val weekEvents = allEvents.filter { it.week == currentWeek }

        // Include championship events (types 3,4) whose dates overlap the week's date range
        val weekStartDate = weekEvents.mapNotNull { parseDate(it.startDate) }.minOrNull()
        val weekEndDate = weekEvents.mapNotNull { parseDate(it.endDate) }.maxOrNull()

        val championshipEvents = if (weekStartDate != null && weekEndDate != null) {
            allEvents.filter { event ->
                event.type in listOf(3, 4) && event.week == null &&
                    datesOverlap(event, weekStartDate, weekEndDate)
            }
        } else {
            emptyList()
        }

        events = (weekEvents + championshipEvents).distinctBy { it.key }
        label = "Happening This Week \u2014 Week ${currentWeek + 1}"
    } else {
        // Offseason fallback: calendar week overlap (Monday-Sunday)
        val monday = today.with(DayOfWeek.MONDAY)
        val sunday = monday.plusDays(6)
        events = allEvents.filter { datesOverlap(it, monday, sunday) }
        label = "Happening This Week"
    }

    // Remove events that have already ended
    val activeEvents = events.filter { event ->
        val end = parseDate(event.endDate)
        end == null || !end.isBefore(today)
    }

    return if (activeEvents.isEmpty()) null else ThisWeekResult(label, activeEvents)
}

internal fun findCurrentCompetitionWeek(allEvents: List<Event>, today: LocalDate): Int? {
    val weekedEvents = allEvents.filter { it.week != null }

    // 1. If any event with a week is happening today → use that week
    val happeningToday = weekedEvents.filter { event ->
        val start = parseDate(event.startDate)
        val end = parseDate(event.endDate)
        start != null && end != null && !today.isBefore(start) && !today.isAfter(end)
    }
    if (happeningToday.isNotEmpty()) {
        return happeningToday.first().week
    }

    // Steps 2 and 3 only apply once the season has started — at least one weeked event
    // must have already ended. This prevents reaching forward from preseason into Week 1.
    val recentlyEnded = weekedEvents
        .filter { event -> parseDate(event.endDate)?.isBefore(today) == true }
        .maxByOrNull { parseDate(it.endDate) ?: LocalDate.MIN }

    val seasonHasStarted = recentlyEnded != null

    // 2. Upcoming event with a week within 7 days → use that week (bridges gap between weeks)
    if (seasonHasStarted) {
        val upcoming = weekedEvents
            .filter { event -> parseDate(event.startDate)?.isAfter(today) == true }
            .minByOrNull { parseDate(it.startDate) ?: LocalDate.MAX }

        if (upcoming != null) {
            val upcomingStart = parseDate(upcoming.startDate)
            if (upcomingStart != null && ChronoUnit.DAYS.between(today, upcomingStart) <= 7) {
                return upcoming.week
            }
        }
    }

    // 3. Recently-ended event with a week within 3 days → use that week
    if (recentlyEnded != null) {
        val endedDate = parseDate(recentlyEnded.endDate)
        if (endedDate != null && ChronoUnit.DAYS.between(endedDate, today) <= 3) {
            return recentlyEnded.week
        }
    }

    return null
}

private fun parseDate(dateStr: String?): LocalDate? =
    dateStr?.let { runCatching { LocalDate.parse(it) }.getOrNull() }

private fun datesOverlap(event: Event, rangeStart: LocalDate, rangeEnd: LocalDate): Boolean {
    val eventStart = parseDate(event.startDate) ?: return false
    val eventEnd = parseDate(event.endDate) ?: return false
    return !eventEnd.isBefore(rangeStart) && !eventStart.isAfter(rangeEnd)
}
