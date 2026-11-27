package com.thebluealliance.android.ui.teams

import com.thebluealliance.android.domain.model.District
import com.thebluealliance.android.domain.model.DistrictRanking
import com.thebluealliance.android.domain.model.Event
import com.thebluealliance.android.domain.model.PlayoffType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class TeamDistrictStandingTest {
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
        assertEquals("Rank 12 · 187 pts", standing.rankAndPoints())
        assertEquals("Rank 40 · 1 pt", standing.copy(rank = 40, points = 1).rankAndPoints())
    }
}
