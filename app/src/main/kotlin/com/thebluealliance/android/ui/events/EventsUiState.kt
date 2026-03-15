package com.thebluealliance.android.ui.events

import com.thebluealliance.android.domain.model.Event
import com.thebluealliance.android.ui.components.SectionHeaderInfo
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.ChronoUnit

data class EventSubSection(val label: String, val events: List<Event>)

data class EventSection(val label: String, val subSections: List<EventSubSection>) {
    val events: List<Event> get() = subSections.flatMap { it.events }
}

sealed interface EventsUiState {
    data object Loading : EventsUiState
    data class Success(
        val sections: List<EventSection>,
        val favoriteEventKeys: Set<String> = emptySet(),
        val districtNames: Map<String, String> = emptyMap(),
    ) : EventsUiState
    data class Error(val message: String) : EventsUiState
}

private data class SectionKey(val sortOrder: Int, val label: String)

private fun eventSectionKey(event: Event, preseasonOver: Boolean): SectionKey {
    return when (event.type) {
        100 -> if (preseasonOver) SectionKey(1500, "Preseason") else SectionKey(-1, "Preseason")
        0, 1, 2, 5 -> {
            val week = event.week ?: return SectionKey(9999, "Other events")
            SectionKey(week, "Week ${week + 1}")
        }
        3, 4 -> SectionKey(1000, "Championship")
        99 -> SectionKey(2000, "Offseason")
        else -> {
            // Unknown type but has a week — group with regular weeks
            if (event.week != null) {
                SectionKey(event.week, "Week ${event.week + 1}")
            } else {
                SectionKey(9999, "Other events")
            }
        }
    }
}

private val eventComparator: Comparator<Event> =
    compareBy({ it.startDate }, { it.district }, { it.name })

fun buildEventSections(
    events: List<Event>,
    today: LocalDate = LocalDate.now(),
    districtNames: Map<String, String> = emptyMap(),
): List<EventSection> {
    val lastPreseasonEnd = events
        .filter { it.type == 100 }
        .mapNotNull { it.endDate?.let { d -> runCatching { LocalDate.parse(d) }.getOrNull() } }
        .maxOrNull()
    val preseasonOver = lastPreseasonEnd != null && today.isAfter(lastPreseasonEnd)

    return events
        .groupBy { eventSectionKey(it, preseasonOver) }
        .entries
        .sortedBy { it.key.sortOrder }
        .map { (key, sectionEvents) ->
            val sortedEvents = sectionEvents.sortedWith(eventComparator)
            val subSections = buildSubSections(sortedEvents, districtNames)
            EventSection(key.label, subSections)
        }
}

private fun buildSubSections(
    events: List<Event>,
    districtNames: Map<String, String>,
): List<EventSubSection> = buildList {
    val regionals = events
        .filter { it.district == null && it.type == 0 }
        .sortedBy { it.name }
    if (regionals.isNotEmpty()) {
        add(EventSubSection("Regional Events", regionals))
    }

    val others = events.filter { it.district == null && it.type != 0 }
    if (others.isNotEmpty()) {
        val offseasons = others.filter { it.type == 99 }
        val nonOffseasonOthers = others.filter { it.type != 99 }

        if (offseasons.isNotEmpty()) {
            offseasons.groupBy {
                it.startDate?.let { s -> runCatching { LocalDate.parse(s) }.getOrNull() }?.month
            }.forEach { (month, monthEvents) ->
                val monthName = month?.name?.lowercase()?.replaceFirstChar { it.uppercase() }
                val label = if (monthName != null) "$monthName Offseason Events" else "Offseason Events"
                add(EventSubSection(label, monthEvents))
            }
        }

        if (nonOffseasonOthers.isNotEmpty()) {
            add(EventSubSection("", nonOffseasonOthers))
        }
    }

    val districts = events.filter { it.district != null }
    if (districts.isNotEmpty()) {
        districts.groupBy { it.district!! }
            .map { (districtKey, districtEvents) ->
                val abbrev = districtKey.replace(Regex("^\\d{4}"), "").lowercase()
                val name = districtNames[districtKey.lowercase()] ?: districtNames[abbrev]

                var label = name ?: abbrev.uppercase()
                if (label.isBlank()) label = districtKey

                // Append "District" if we're falling back to a code/abbreviation
                if (name == null && !label.contains("District", ignoreCase = true)) {
                    label = "$label District"
                }

                EventSubSection(label, districtEvents)
            }
            .sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it.label })
            .let { addAll(it) }
    }
}

fun buildHeaderInfos(
    sections: List<EventSection>,
    favoriteEventKeys: Set<String>,
): List<SectionHeaderInfo> = buildList {
    var index = 0
    // Top-level Favorites section
    val totalFavorites = sections.sumOf { section ->
        section.events.count { it.key in favoriteEventKeys }
    }
    if (totalFavorites > 0) {
        add(SectionHeaderInfo("favorites_header", "Favorites", index))
        index += 1 + totalFavorites // header + favorite items
    }
    // Week sections
    sections.forEach { section ->
        val headerKey = "header_${section.label}"
        add(SectionHeaderInfo(headerKey, section.label, index))
        index += 1 // header item
        // Per-week favorites sub-section
        val favCount = section.events.count { it.key in favoriteEventKeys }
        if (favCount > 0) {
            index += 1 + favCount // sub-header + favorite items
        }
        // Regular sub-sections
        index += section.subSections.sumOf {
            (if (it.label.isNotEmpty()) 1 else 0) + it.events.size
        }
    }
}

data class ThisWeekResult(val label: String, val subSections: List<EventSubSection>) {
    val events: List<Event> get() = subSections.flatMap { it.events }
}

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
    districtNames: Map<String, String> = emptyMap(),
): ThisWeekResult? {
    if (selectedYear != today.year) return null

    val currentWeek = findCurrentCompetitionWeek(allEvents, today)

    val label: String
    val rawEvents: List<Event>

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

        rawEvents = (weekEvents + championshipEvents).distinctBy { it.key }
        label = "Upcoming This Week \u2014 Week ${currentWeek + 1}"
    } else {
        // Offseason fallback: calendar week overlap (Monday-Sunday)
        val monday = today.with(DayOfWeek.MONDAY)
        val sunday = monday.plusDays(6)
        rawEvents = allEvents.filter { datesOverlap(it, monday, sunday) }
        label = "Upcoming This Week"
    }

    // Remove events that have already ended
    val activeEvents = rawEvents.filter { event ->
        val end = parseDate(event.endDate)
        end == null || !end.isBefore(today)
    }

    if (activeEvents.isEmpty()) return null

    val sortedActiveEvents = activeEvents.sortedWith(eventComparator)
    val subSections = buildSubSections(sortedActiveEvents, districtNames)

    return ThisWeekResult(label, subSections)
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

