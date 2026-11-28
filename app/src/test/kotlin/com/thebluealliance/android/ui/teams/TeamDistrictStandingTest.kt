package com.thebluealliance.android.ui.teams

import com.thebluealliance.android.domain.model.District
import com.thebluealliance.android.domain.model.DistrictRanking
import com.thebluealliance.android.domain.model.Event
import com.thebluealliance.android.domain.model.PlayoffType
import com.thebluealliance.android.ui.common.ResourceQuantityStrings
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class TeamDistrictStandingTest {
    private val strings = ResourceQuantityStrings

    private fun event(
        key: String,
        district: String?,
        year: Int = 2026,
    ) = Event(
        key = key,
        name = key,
        eventCode = key.drop(4),
        year = year,
        type = null,
        district = district,
        city = null,
        state = null,
        country = null,
        startDate = null,
        endDate = null,
        week = null,
        shortName = null,
        website = null,
        timezone = null,
        locationName = null,
        address = null,
        gmapsUrl = null,
        webcasts = emptyList(),
        playoffType = PlayoffType.BRACKET_8_TEAM,
    )

    private fun ranking(
        teamKey: String,
        rank: Int,
        points: Double,
        districtKey: String = "2026fim",
    ) = DistrictRanking(districtKey, teamKey, rank, points, rookieBonus = 0.0)

    private val fim = District("2026fim", "fim", "FIRST In Michigan", 2026)

    @Test
    fun `district keys come from the year's events, once each`() {
        val events =
            listOf(
                event("2026mitry", "2026fim"),
                event("2026micmp", "2026fim"),
                event("2026onham", "2026ont"),
                event("2026cmptx", null),
                event("2025mitry", "2025fim", year = 2025),
            )
        assertEquals(listOf("2026fim", "2026ont"), teamDistrictKeys(events, 2026))
    }

    @Test
    fun `a team with no district events has no district keys`() {
        assertEquals(emptyList<String>(), teamDistrictKeys(listOf(event("2026txhou", null)), 2026))
    }

    @Test
    fun `maps the team's ranking to rank, whole points and district name`() {
        val snapshot =
            DistrictRankingsSnapshot(
                "2026fim",
                fim,
                listOf(ranking("frc33", 1, 210.0), ranking("frc254", 12, 187.0)),
            )
        assertEquals(
            TeamDistrictStanding("2026fim", "FIRST In Michigan", rank = 12, points = 187),
            teamDistrictStanding("frc254", listOf(snapshot)),
        )
    }

    @Test
    fun `picks the district whose rankings list the team`() {
        val ont =
            DistrictRankingsSnapshot(
                "2026ont",
                null,
                listOf(ranking("frc1114", 1, 200.0, "2026ont")),
            )
        val fimSnapshot =
            DistrictRankingsSnapshot("2026fim", fim, listOf(ranking("frc254", 3, 150.0)))
        assertEquals(
            "2026fim",
            teamDistrictStanding("frc254", listOf(ont, fimSnapshot))?.districtKey,
        )
    }

    @Test
    fun `falls back to the abbreviation before the district's name loads`() {
        val snapshot =
            DistrictRankingsSnapshot("2026fim", null, listOf(ranking("frc254", 3, 150.0)))
        assertEquals("FIM", teamDistrictStanding("frc254", listOf(snapshot))?.districtName)
    }

    @Test
    fun `hidden while rankings are empty or loading`() {
        assertNull(
            teamDistrictStanding(
                "frc254",
                listOf(DistrictRankingsSnapshot("2026fim", fim, emptyList())),
            ),
        )
    }

    @Test
    fun `hidden before any district event has scored`() {
        val rookie = DistrictRanking("2027fim", "frc9999", 1, 10.0, rookieBonus = 10.0)
        val snapshot =
            DistrictRankingsSnapshot("2027fim", fim, listOf(rookie, ranking("frc254", 135, 0.0)))
        assertNull(teamDistrictStanding("frc254", listOf(snapshot)))
    }

    @Test
    fun `shows a team yet to play once the district has points`() {
        val snapshot =
            DistrictRankingsSnapshot(
                "2026fim",
                fim,
                listOf(ranking("frc33", 1, 60.0), ranking("frc254", 135, 0.0)),
            )
        assertEquals(0, teamDistrictStanding("frc254", listOf(snapshot))?.points)
    }

    @Test
    fun `hidden for a team the district doesn't rank`() {
        val snapshot = DistrictRankingsSnapshot("2026fim", fim, listOf(ranking("frc33", 1, 210.0)))
        assertNull(teamDistrictStanding("frc254", listOf(snapshot)))
    }

    @Test
    fun `hidden for a team with no districts`() {
        assertNull(teamDistrictStanding("frc254", emptyList()))
    }

    @Test
    fun `rank and points read as one phrase`() {
        val standing = TeamDistrictStanding("2026fim", "FIRST In Michigan", rank = 12, points = 187)
        assertEquals("Rank 12 · 187 pts", standing.rankAndPoints(strings))
        assertEquals("Rank 40 · 1 pt", standing.copy(rank = 40, points = 1).rankAndPoints(strings))
    }

    @Test
    fun `the district link covers exactly the name in the line`() {
        val standing = TeamDistrictStanding("2026ca", "FIRST California", rank = 2, points = 364)
        val line = districtStandingLine(standing, strings)
        assertEquals("District: FIRST California · Rank 2 · 364 pts", line)
        assertEquals("FIRST California", line.substring(districtLinkRange(standing)))
    }

    @Test
    fun `falls back to the abbreviation when the district's name is blank`() {
        val blank = District("2026fim", "fim", "", 2026)
        val snapshot =
            DistrictRankingsSnapshot("2026fim", blank, listOf(ranking("frc254", 3, 150.0)))
        assertEquals("FIM", teamDistrictStanding("frc254", listOf(snapshot))?.districtName)
    }

    @Test
    fun `a layout of the abbreviation can't place the full name's link`() {
        val short = TeamDistrictStanding("2026ne", "NE", rank = 5, points = 120)
        val full = short.copy(districtName = "FIRST In New England")
        assertFalse(
            linkFitsLayout(
                laidOutText = districtStandingLine(short, strings),
                text = districtStandingLine(full, strings),
                link = districtLinkRange(full),
            ),
        )
    }

    @Test
    fun `a layout of the same line places its link`() {
        val standing =
            TeamDistrictStanding("2026ne", "FIRST In New England", rank = 5, points = 120)
        val line = districtStandingLine(standing, strings)
        assertTrue(linkFitsLayout(line, line, districtLinkRange(standing)))
    }

    @Test
    fun `an empty link is never placed`() {
        val line = "District:  · Rank 5 · 120 pts"
        assertFalse(linkFitsLayout(line, line, 10 until 10))
    }
}
