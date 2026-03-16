package com.thebluealliance.android.domain.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class AllianceTest {

    private fun createAlliance(
        playoffStatus: String? = null,
        playoffLevel: String? = null,
        playoffDoubleElimRound: String? = null,
    ) = Alliance(
        eventKey = "2026test",
        number = 1,
        name = null,
        picks = listOf("frc1", "frc2", "frc3"),
        declines = emptyList(),
        backupIn = null,
        backupOut = null,
        playoffStatus = playoffStatus,
        playoffLevel = playoffLevel,
        playoffDoubleElimRound = playoffDoubleElimRound,
    )

    @Test
    fun `playoff summary prefers double elim round when present`() {
        val alliance = createAlliance(
            playoffStatus = "eliminated",
            playoffLevel = "sf",
            playoffDoubleElimRound = "Round 5",
        )

        assertEquals("Eliminated in Round 5", alliance.playoffSummary)
    }

    @Test
    fun `playoff summary renders won status as winner`() {
        val alliance = createAlliance(
            playoffStatus = "won",
            playoffLevel = "f",
        )

        assertEquals("Winner 🏆", alliance.playoffSummary)
    }

    @Test
    fun `playoff summary shows comp level even when status missing`() {
        val alliance = createAlliance(playoffLevel = "qf")

        assertEquals("In the Quarterfinals", alliance.playoffSummary)
    }

    @Test
    fun `playoff summary returns null when no playoff info exists`() {
        val alliance = createAlliance()

        assertNull(alliance.playoffSummary)
    }

    @Test
    fun `playoff summary uses in the for comp level fallback`() {
        val alliance = createAlliance(
            playoffStatus = "playing",
            playoffLevel = "sf",
        )

        assertEquals("Playing in the Semifinals", alliance.playoffSummary)
    }

    @Test
    fun `playoff summary uses capitalized status when only status exists`() {
        val alliance = createAlliance(playoffStatus = "eliminated")

        assertEquals("Eliminated", alliance.playoffSummary)
    }
}


