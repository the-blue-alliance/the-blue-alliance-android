package com.thebluealliance.android.ui.events

import com.thebluealliance.android.domain.model.Event
import com.thebluealliance.android.domain.model.PlayoffType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import java.time.LocalDate

class EventsUiStateTest {

    private fun makeEvent(
        key: String = "2026test",
        name: String = "Test Event",
        year: Int = 2026,
        type: Int? = 0,
        week: Int? = null,
        startDate: String? = null,
        endDate: String? = null,
    ) = Event(
        key = key,
        name = name,
        eventCode = key.removePrefix("${year}"),
        year = year,
        type = type,
        district = null,
        city = null,
        state = null,
        country = null,
        startDate = startDate,
        endDate = endDate,
        week = week,
        shortName = null,
        website = null,
        timezone = null,
        locationName = null,
        address = null,
        gmapsUrl = null,
        webcasts = emptyList(),
        playoffType = PlayoffType.BRACKET_8_TEAM,
    )

    // --- buildEventSections: preseason ordering ---

    @Test
    fun `preseason appears first when today is before preseason ends`() {
        val today = LocalDate.of(2026, 2, 20) // During preseason
        val events = listOf(
            makeEvent(key = "2026pre1", type = 100, startDate = "2026-02-14", endDate = "2026-02-21"),
            makeEvent(key = "2026wk1", type = 0, week = 0, startDate = "2026-03-04", endDate = "2026-03-07"),
            makeEvent(key = "2026cmp", type = 3, startDate = "2026-04-15", endDate = "2026-04-18"),
        )

        val sections = buildEventSections(events, today)

        assertEquals("Preseason", sections.first().label)
        assertEquals(listOf("Preseason", "Week 1", "Championship"), sections.map { it.label })
    }

    @Test
    fun `preseason appears after championship when today is after last preseason event`() {
        val today = LocalDate.of(2026, 3, 10) // After preseason ended
        val events = listOf(
            makeEvent(key = "2026pre1", type = 100, startDate = "2026-02-14", endDate = "2026-02-21"),
            makeEvent(key = "2026wk1", type = 0, week = 0, startDate = "2026-03-04", endDate = "2026-03-07"),
            makeEvent(key = "2026cmp", type = 3, startDate = "2026-04-15", endDate = "2026-04-18"),
            makeEvent(key = "2026off1", type = 99, startDate = "2026-07-15", endDate = "2026-07-17"),
        )

        val sections = buildEventSections(events, today)

        assertEquals(listOf("Week 1", "Championship", "Preseason", "Offseason"), sections.map { it.label })
    }

    // --- Regular season: event happening today ---

    @Test
    fun `regular season event happening today returns correct week label`() {
        val today = LocalDate.of(2026, 3, 11) // Wednesday
        val events = listOf(
            makeEvent(key = "2026abc", week = 2, startDate = "2026-03-09", endDate = "2026-03-14"),
            makeEvent(key = "2026def", week = 2, startDate = "2026-03-10", endDate = "2026-03-13"),
            makeEvent(key = "2026ghi", week = 3, startDate = "2026-03-16", endDate = "2026-03-20"),
        )

        val result = computeThisWeekEvents(events, today, selectedYear = 2026)

        assertNotNull(result)
        assertEquals("Upcoming This Week \u2014 Week 3", result!!.label) // week=2 → display "Week 3"
        assertEquals(2, result.events.size)
        assertEquals(setOf("2026abc", "2026def"), result.events.map { it.key }.toSet())
    }

    // --- Sunday gap between weeks ---

    @Test
    fun `sunday gap uses upcoming week when season has started`() {
        val today = LocalDate.of(2026, 3, 15) // Sunday between weeks
        val events = listOf(
            // Week 2 ended yesterday — season has started
            makeEvent(key = "2026abc", week = 2, startDate = "2026-03-09", endDate = "2026-03-14"),
            makeEvent(key = "2026def", week = 3, startDate = "2026-03-18", endDate = "2026-03-21"),
        )

        val result = computeThisWeekEvents(events, today, selectedYear = 2026)

        assertNotNull(result)
        assertEquals("Upcoming This Week \u2014 Week 4", result!!.label) // week=3 → display "Week 4"
        assertEquals(1, result.events.size)
        assertEquals("2026def", result.events[0].key)
    }

    @Test
    fun `sunday after last event with no upcoming events returns null`() {
        val today = LocalDate.of(2026, 3, 15) // Sunday, all week 2 events ended
        val events = listOf(
            makeEvent(key = "2026abc", week = 2, startDate = "2026-03-09", endDate = "2026-03-14"),
            // No upcoming events with a week
        )

        val result = computeThisWeekEvents(events, today, selectedYear = 2026)

        // Week 2 is detected via recently-ended, but all its events have ended → filtered out
        assertNull(result)
    }

    // --- Preseason: don't reach forward into regular season ---

    @Test
    fun `preseason does not show upcoming regular season week`() {
        val today = LocalDate.of(2026, 2, 27) // Friday before season starts
        val events = listOf(
            // Preseason events happening tomorrow (type=100, no week)
            makeEvent(
                key = "2026pre1", type = 100, week = null,
                startDate = "2026-02-28", endDate = "2026-02-28",
            ),
            // Week 1 events starting next week (week=0 in API)
            makeEvent(key = "2026wk1", week = 0, startDate = "2026-03-04", endDate = "2026-03-07"),
        )

        val result = computeThisWeekEvents(events, today, selectedYear = 2026)

        // Should use calendar-week fallback showing preseason, NOT jump to Week 1
        assertNotNull(result)
        assertEquals("Upcoming This Week", result!!.label)
        assertEquals(1, result.events.size)
        assertEquals("2026pre1", result.events[0].key)
    }

    @Test
    fun `preseason with no events this calendar week returns null`() {
        val today = LocalDate.of(2026, 2, 20) // Week before any preseason events
        val events = listOf(
            makeEvent(
                key = "2026pre1", type = 100, week = null,
                startDate = "2026-02-28", endDate = "2026-02-28",
            ),
            makeEvent(key = "2026wk1", week = 0, startDate = "2026-03-04", endDate = "2026-03-07"),
        )

        val result = computeThisWeekEvents(events, today, selectedYear = 2026)

        assertNull(result)
    }

    // --- Offseason fallback ---

    @Test
    fun `offseason uses calendar week fallback`() {
        val today = LocalDate.of(2026, 7, 15) // Wednesday in July
        val events = listOf(
            // Offseason events have no week field
            makeEvent(
                key = "2026off1", type = 99, week = null,
                startDate = "2026-07-13", endDate = "2026-07-15",
            ),
            makeEvent(
                key = "2026off2", type = 99, week = null,
                startDate = "2026-07-20", endDate = "2026-07-22",
            ),
        )

        val result = computeThisWeekEvents(events, today, selectedYear = 2026)

        assertNotNull(result)
        assertEquals("Upcoming This Week", result!!.label)
        assertEquals(1, result.events.size)
        assertEquals("2026off1", result.events[0].key)
    }

    // --- Non-current year ---

    @Test
    fun `non-current year returns null`() {
        val today = LocalDate.of(2026, 3, 11)
        val events = listOf(
            makeEvent(key = "2024abc", year = 2024, week = 2, startDate = "2024-03-11", endDate = "2024-03-14"),
        )

        val result = computeThisWeekEvents(events, today, selectedYear = 2024)

        assertNull(result)
    }

    // --- Championship events included via date overlap ---

    @Test
    fun `championship events included when dates overlap current week`() {
        val today = LocalDate.of(2026, 4, 15) // During championship week
        val events = listOf(
            makeEvent(key = "2026reg", week = 6, type = 0, startDate = "2026-04-13", endDate = "2026-04-17"),
            makeEvent(
                key = "2026cmp", week = null, type = 3,
                startDate = "2026-04-14", endDate = "2026-04-18",
            ),
            makeEvent(
                key = "2026cmp2", week = null, type = 4,
                startDate = "2026-04-20", endDate = "2026-04-22",
            ),
        )

        val result = computeThisWeekEvents(events, today, selectedYear = 2026)

        assertNotNull(result)
        assertEquals("Upcoming This Week \u2014 Week 7", result!!.label) // week=6 → "Week 7"
        assertEquals(setOf("2026reg", "2026cmp"), result.events.map { it.key }.toSet())
        // cmp2 does NOT overlap the week's date range, so excluded
    }

    // --- Ended events are filtered out ---

    @Test
    fun `ended events are excluded from results`() {
        val today = LocalDate.of(2026, 3, 13) // Friday, mid-week
        val events = listOf(
            // This 3-day event ended yesterday
            makeEvent(key = "2026short", week = 2, startDate = "2026-03-09", endDate = "2026-03-12"),
            // This event is still going
            makeEvent(key = "2026long", week = 2, startDate = "2026-03-09", endDate = "2026-03-14"),
        )

        val result = computeThisWeekEvents(events, today, selectedYear = 2026)

        assertNotNull(result)
        assertEquals(1, result!!.events.size)
        assertEquals("2026long", result.events[0].key)
    }

    // --- No events returns null ---

    @Test
    fun `no events returns null`() {
        val today = LocalDate.of(2026, 3, 11)
        val result = computeThisWeekEvents(emptyList(), today, selectedYear = 2026)
        assertNull(result)
    }

    @Test
    fun `no events this week returns null`() {
        val today = LocalDate.of(2026, 6, 10) // No events anywhere near this date
        val events = listOf(
            makeEvent(key = "2026abc", week = 2, startDate = "2026-03-09", endDate = "2026-03-14"),
        )

        val result = computeThisWeekEvents(events, today, selectedYear = 2026)

        assertNull(result)
    }

    // --- findCurrentCompetitionWeek ---

    @Test
    fun `findCurrentCompetitionWeek prefers happening-today over upcoming`() {
        val today = LocalDate.of(2026, 3, 14) // Saturday, last day of week 2
        val events = listOf(
            makeEvent(key = "2026abc", week = 2, startDate = "2026-03-09", endDate = "2026-03-14"),
            makeEvent(key = "2026def", week = 3, startDate = "2026-03-18", endDate = "2026-03-21"),
        )

        val week = findCurrentCompetitionWeek(events, today)

        assertEquals(2, week)
    }

    @Test
    fun `findCurrentCompetitionWeek returns null when gap is too large`() {
        val today = LocalDate.of(2026, 5, 1) // Long after any events
        val events = listOf(
            makeEvent(key = "2026abc", week = 2, startDate = "2026-03-09", endDate = "2026-03-14"),
        )

        val week = findCurrentCompetitionWeek(events, today)

        assertNull(week)
    }
}
