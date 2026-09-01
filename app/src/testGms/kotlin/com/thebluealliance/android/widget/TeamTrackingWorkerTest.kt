package com.thebluealliance.android.widget

import com.thebluealliance.android.domain.model.CompLevel
import com.thebluealliance.android.domain.model.Match
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class TeamTrackingWorkerTest {
    private val trackedTeam = "frc254"

    private fun match(
        key: String,
        redTeamKeys: List<String> = listOf("frc254", "frc1", "frc2"),
        blueTeamKeys: List<String> = listOf("frc3", "frc4", "frc5"),
        redSurrogateTeamKeys: List<String> = emptyList(),
        blueSurrogateTeamKeys: List<String> = emptyList(),
        redScore: Int = 100,
        blueScore: Int = 50,
        winningAlliance: String? = "red",
    ) = Match(
        key = key,
        eventKey = "2026test",
        compLevel = CompLevel.QUAL,
        matchNumber = 1,
        setNumber = 1,
        time = null,
        predictedTime = null,
        actualTime = null,
        redTeamKeys = redTeamKeys,
        redSurrogateTeamKeys = redSurrogateTeamKeys,
        redScore = redScore,
        blueTeamKeys = blueTeamKeys,
        blueSurrogateTeamKeys = blueSurrogateTeamKeys,
        blueScore = blueScore,
        winningAlliance = winningAlliance,
    )

    @Test
    fun `computeRecord counts wins losses and ties for played matches`() {
        val matches =
            listOf(
                match("2026test_qm1", winningAlliance = "red"),
                match("2026test_qm2", winningAlliance = "blue"),
                match("2026test_qm3", winningAlliance = ""),
                match("2026test_qm4", redScore = -1, blueScore = -1, winningAlliance = null),
            )

        assertEquals("1-1-1", TeamTrackingWorker.computeRecord(matches, trackedTeam))
    }

    @Test
    fun `computeRecord skips matches where tracked team is a surrogate`() {
        val matches =
            listOf(
                match("2026test_qm1", winningAlliance = "red"),
                match(
                    "2026test_qm2",
                    redSurrogateTeamKeys = listOf(trackedTeam),
                    winningAlliance = "red",
                ),
                match("2026test_qm3", winningAlliance = "blue"),
            )

        assertEquals("1-1-0", TeamTrackingWorker.computeRecord(matches, trackedTeam))
    }

    @Test
    fun `computeRecord skips surrogate appearance on the blue alliance`() {
        val matches =
            listOf(
                match(
                    "2026test_qm1",
                    redTeamKeys = listOf("frc1", "frc2", "frc3"),
                    blueTeamKeys = listOf(trackedTeam, "frc4", "frc5"),
                    blueSurrogateTeamKeys = listOf(trackedTeam),
                    winningAlliance = "blue",
                ),
            )

        assertEquals("0-0-0", TeamTrackingWorker.computeRecord(matches, trackedTeam))
    }

    @Test
    fun `computeRecord still counts teammates when another team is the surrogate`() {
        val matches =
            listOf(
                match(
                    "2026test_qm1",
                    redSurrogateTeamKeys = listOf("frc1"),
                    winningAlliance = "red",
                ),
            )

        assertEquals("1-0-0", TeamTrackingWorker.computeRecord(matches, trackedTeam))
    }
}
