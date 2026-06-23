package com.thebluealliance.android.ui.mytba

import com.thebluealliance.android.domain.model.CompLevel
import com.thebluealliance.android.domain.model.Event
import com.thebluealliance.android.domain.model.EventType
import com.thebluealliance.android.domain.model.Match
import com.thebluealliance.android.domain.model.PlayoffType
import com.thebluealliance.android.domain.model.Team
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class MyTBADisplayNamesTest {
    private fun makeTeam(
        key: String = "frc254",
        number: Int = 254,
        name: String? = "NASA Ames Research Center & Team 254",
        nickname: String? = "The Cheesy Poofs",
    ) = Team(
        key = key,
        number = number,
        name = name,
        nickname = nickname,
        city = null,
        state = null,
        country = null,
        rookieYear = null,
    )

    private fun makeEvent(
        key: String = "2026casj",
        name: String = "Silicon Valley Regional",
        year: Int = 2026,
        playoffType: PlayoffType = PlayoffType.DOUBLE_ELIM_8_TEAM,
    ) = Event(
        key = key,
        name = name,
        eventCode = key.removePrefix("$year"),
        year = year,
        type = EventType.REGIONAL,
        district = null,
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
        playoffType = playoffType,
    )

    private fun makeMatch(
        key: String = "2026casj_qm42",
        eventKey: String = "2026casj",
        compLevel: CompLevel = CompLevel.QUAL,
        matchNumber: Int = 42,
        setNumber: Int = 1,
    ) = Match(
        key = key,
        eventKey = eventKey,
        compLevel = compLevel,
        matchNumber = matchNumber,
        setNumber = setNumber,
        time = null,
        predictedTime = null,
        actualTime = null,
        redTeamKeys = emptyList(),
        redScore = -1,
        blueTeamKeys = emptyList(),
        blueScore = -1,
        winningAlliance = null,
    )

    @Test
    fun `team maps to number and nickname`() {
        val names = buildMyTBADisplayNames(listOf(makeTeam()), emptyList(), emptyList())

        assertEquals("254 - The Cheesy Poofs", names["frc254"])
    }

    @Test
    fun `team without nickname falls back to name then number`() {
        val names =
            buildMyTBADisplayNames(
                listOf(
                    makeTeam(key = "frc1", number = 1, nickname = null, name = "The Juggernauts"),
                    makeTeam(key = "frc2", number = 2, nickname = null, name = null),
                ),
                emptyList(),
                emptyList(),
            )

        assertEquals("1 - The Juggernauts", names["frc1"])
        assertEquals("2", names["frc2"])
    }

    @Test
    fun `event maps to year and name`() {
        val names = buildMyTBADisplayNames(emptyList(), listOf(makeEvent()), emptyList())

        assertEquals("2026 Silicon Valley Regional", names["2026casj"])
    }

    @Test
    fun `match with cached event maps to match label and event name`() {
        val names = buildMyTBADisplayNames(emptyList(), listOf(makeEvent()), listOf(makeMatch()))

        assertEquals("Qual 42 - 2026 Silicon Valley Regional", names["2026casj_qm42"])
    }

    @Test
    fun `match label uses event playoff type`() {
        val match =
            makeMatch(
                key = "2026casj_sf8m1",
                compLevel = CompLevel.SEMIFINAL,
                matchNumber = 1,
                setNumber = 8,
            )

        val names = buildMyTBADisplayNames(emptyList(), listOf(makeEvent()), listOf(match))

        assertEquals("Match 8 (Round 2) - 2026 Silicon Valley Regional", names["2026casj_sf8m1"])
    }

    @Test
    fun `match without cached event is omitted so callers fall back to the raw key`() {
        val names = buildMyTBADisplayNames(emptyList(), emptyList(), listOf(makeMatch()))

        assertNull(names["2026casj_qm42"])
    }
}
