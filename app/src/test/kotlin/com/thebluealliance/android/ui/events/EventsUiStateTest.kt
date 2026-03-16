package com.thebluealliance.android.ui.events

import com.thebluealliance.android.domain.model.Event
import com.thebluealliance.android.domain.model.PlayoffType
import com.thebluealliance.android.ui.components.SectionHeaderInfo
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
        district: String? = null,
    ) = Event(
        key = key,
        name = name,
        eventCode = key.removePrefix("${year}"),
        year = year,
        type = type,
        district = district,
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

    // --- buildHeaderInfos: index calculation ---

    @Test
    fun `buildHeaderInfos calculates indices correctly with favorites`() {
        val favoriteKeys = setOf("w1r1", "w2o1")

        val sections = listOf(
            EventSection(
                label = "Week 1",
                subSections = listOf(
                    EventSubSection("Regional", listOf(makeEvent(key = "w1r1"), makeEvent(key = "w1r2"))),
                    EventSubSection("District", listOf(makeEvent(key = "w1d1")))
                )
            ),
            EventSection(
                label = "Week 2",
                subSections = listOf(
                    EventSubSection("", listOf(makeEvent(key = "w2o1"))) // Empty label = no header item
                )
            )
        )

        val infos = buildHeaderInfos(sections, favoriteKeys)

        assertEquals(3, infos.size)

        // 1. Top-level Favorites Header at index 0
        assertEquals("Favorites", infos[0].label)
        assertEquals(0, infos[0].itemIndex)

        // Top-level Favorites items:
        //   header (1) + w1r1 (1) + w2o1 (1) = 3

        // 2. Week 1 Header at index 3
        assertEquals("Week 1", infos[1].label)
        assertEquals(3, infos[1].itemIndex)

        // Week 1 items:
        //   header (1)
        //   + Favorites sub-header (1) + w1r1 (1) = 2
        //   + Regional header (1) + w1r1, w1r2 (2) = 3
        //   + District header (1) + w1d1 (1) = 2
        //   Total = 1 + 2 + 3 + 2 = 8

        // 3. Week 2 Header at index 11
        assertEquals("Week 2", infos[2].label)
        assertEquals(11, infos[2].itemIndex)
    }

    @Test
    fun `buildHeaderInfos with no favorites has no extra sub-sections`() {
        val sections = listOf(
            EventSection(
                label = "Week 1",
                subSections = listOf(
                    EventSubSection("Regional", listOf(makeEvent(key = "w1r1")))
                )
            ),
            EventSection(
                label = "Week 2",
                subSections = listOf(
                    EventSubSection("", listOf(makeEvent(key = "w2o1")))
                )
            )
        )

        val infos = buildHeaderInfos(sections, emptySet())

        assertEquals(2, infos.size)

        // Week 1: header (1) + Regional header (1) + w1r1 (1) = 3
        assertEquals(0, infos[0].itemIndex)
        assertEquals(3, infos[1].itemIndex)
    }

    // --- buildEventSections: sort order within sections ---

    @Test
    fun `regional events within a section are sorted alphabetically by name`() {
        val events = listOf(
            makeEvent(key = "2026z", name = "Zebra Event", week = 0, startDate = "2026-03-04", endDate = "2026-03-07"),
            makeEvent(key = "2026a", name = "Alpha Event", week = 0, startDate = "2026-03-06", endDate = "2026-03-08"),
            makeEvent(key = "2026m", name = "Middle Event", week = 0, startDate = "2026-03-04", endDate = "2026-03-07"),
        )

        val sections = buildEventSections(events)
        val week1 = sections.first { it.label == "Week 1" }

        // Sorted by name regardless of start date
        assertEquals(listOf("Alpha Event", "Middle Event", "Zebra Event"), week1.events.map { it.name })
    }

    // --- buildEventSections: sub-section labels ---

    @Test
    fun `regional events have Regional Events sub-section label`() {
        val events = listOf(
            makeEvent(name = "Regional 1", type = 0, week = 0),
        )
        val sections = buildEventSections(events)
        val week1 = sections.first { it.label == "Week 1" }

        assertEquals(1, week1.subSections.size)
        assertEquals("Regional Events", week1.subSections[0].label)
    }

    @Test
    fun `championship events have empty sub-section label`() {
        val events = listOf(
            makeEvent(name = "CMP", type = 3, week = null),
        )
        val sections = buildEventSections(events)
        val cmp = sections.first { it.label == "Championship" }

        assertEquals(1, cmp.subSections.size)
        assertEquals("", cmp.subSections[0].label)
    }

    @Test
    fun `district events use district name or fallback`() {
        val events = listOf(
            makeEvent(name = "District Event", type = 1, week = 0, district = "2026ne"),
        )
        val sections = buildEventSections(events, districtNames = mapOf("2026ne" to "New England"))
        val week1 = sections.first { it.label == "Week 1" }

        assertEquals(1, week1.subSections.size)
        assertEquals("New England", week1.subSections[0].label)
    }

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

    // --- computeThisWeekEvents (used by DistrictDetailScreen) ---

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
        assertEquals("Upcoming This Week \u2014 Week 3", result!!.label)
        assertEquals(2, result.events.size)
        assertEquals(setOf("2026abc", "2026def"), result.events.map { it.key }.toSet())
    }

    @Test
    fun `non-current year returns null`() {
        val today = LocalDate.of(2026, 3, 11)
        val events = listOf(
            makeEvent(key = "2024abc", year = 2024, week = 2, startDate = "2024-03-11", endDate = "2024-03-14"),
        )
        assertNull(computeThisWeekEvents(events, today, selectedYear = 2024))
    }

    @Test
    fun `no events returns null`() {
        val today = LocalDate.of(2026, 3, 11)
        assertNull(computeThisWeekEvents(emptyList(), today, selectedYear = 2026))
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
