package com.thebluealliance.android.domain

import com.thebluealliance.android.domain.model.CompLevel
import com.thebluealliance.android.domain.model.Match
import com.thebluealliance.android.domain.model.MatchGroup
import com.thebluealliance.android.domain.model.PlayoffType
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.assertInstanceOf

class MatchGroupExtensionTest {

    private fun createMatch(
        compLevel: CompLevel = CompLevel.QUAL,
        matchNumber: Int = 1,
        setNumber: Int = 1,
    ): Match = Match(
        key = "2023test_${compLevel.code}m${matchNumber}",
        eventKey = "2023test",
        compLevel = compLevel,
        matchNumber = matchNumber,
        setNumber = setNumber,
        time = null,
        actualTime = null,
        predictedTime = null,
        redTeamKeys = listOf("frc1", "frc2", "frc3"),
        redScore = 100,
        blueTeamKeys = listOf("frc4", "frc5", "frc6"),
        blueScore = 110,
        winningAlliance = "blue",
    )

    @Nested
    inner class GetGroupTests {
        @Test
        fun `returns CompetitionLevel for QUAL matches`() {
            val match = createMatch(compLevel = CompLevel.QUAL)
            val result = match.getGroup(PlayoffType.BRACKET_8_TEAM)
            assertInstanceOf<MatchGroup.CompetitionLevel>(result)
            assertEquals(CompLevel.QUAL, result.compLevel)
        }

        @Test
        fun `returns CompetitionLevel for OTHER comp level`() {
            val match = createMatch(compLevel = CompLevel.OTHER)
            val result = match.getGroup(PlayoffType.BRACKET_8_TEAM)
            val compLevel = assertInstanceOf<MatchGroup.CompetitionLevel>(result)
            assertEquals(CompLevel.OTHER, compLevel.compLevel)
        }

        @Nested
        inner class DoubleEliminationTests {
            @Test
            fun `DOUBLE_ELIM_8_TEAM with SEMIFINAL returns DoubleEliminationRound`() {
                val match = createMatch(compLevel = CompLevel.SEMIFINAL, setNumber = 1)
                val result = match.getGroup(PlayoffType.DOUBLE_ELIM_8_TEAM)
                val round = assertInstanceOf<MatchGroup.DoubleEliminationRound.Round>(result)
                assertEquals(1, round.number)
            }

            @Test
            fun `DOUBLE_ELIM_8_TEAM with FINAL returns Finals`() {
                val match = createMatch(compLevel = CompLevel.FINAL, setNumber = 1)
                val result = match.getGroup(PlayoffType.DOUBLE_ELIM_8_TEAM)
                assertInstanceOf<MatchGroup.DoubleEliminationRound.Finals>(result)
            }

            @Test
            fun `DOUBLE_ELIM_4_TEAM with SEMIFINAL returns DoubleEliminationRound`() {
                val match = createMatch(compLevel = CompLevel.SEMIFINAL, setNumber = 1)
                val result = match.getGroup(PlayoffType.DOUBLE_ELIM_4_TEAM)
                val round = assertInstanceOf<MatchGroup.DoubleEliminationRound.Round>(result)
                assertEquals(1, round.number)
            }

            @Test
            fun `DOUBLE_ELIM_4_TEAM with FINAL returns Finals`() {
                val match = createMatch(compLevel = CompLevel.FINAL, setNumber = 1)
                val result = match.getGroup(PlayoffType.DOUBLE_ELIM_4_TEAM)
                assertInstanceOf<MatchGroup.DoubleEliminationRound.Finals>(result)
            }

            @Test
            fun `LEGACY_DOUBLE_ELIM_8_TEAM with OCTOFINAL returns DoubleEliminationRound`() {
                val match = createMatch(compLevel = CompLevel.OCTOFINAL, setNumber = 1)
                val result = match.getGroup(PlayoffType.LEGACY_DOUBLE_ELIM_8_TEAM)
                assertInstanceOf<MatchGroup.DoubleEliminationRound.Round>(result)
            }

            @Test
            fun `LEGACY_DOUBLE_ELIM_8_TEAM with QUARTERFINAL returns DoubleEliminationRound`() {
                val match = createMatch(compLevel = CompLevel.QUARTERFINAL, setNumber = 1)
                val result = match.getGroup(PlayoffType.LEGACY_DOUBLE_ELIM_8_TEAM)
                assertInstanceOf<MatchGroup.DoubleEliminationRound.Round>(result)
            }

            @Test
            fun `Non-double-elim playoff type returns CompetitionLevel for playoff comp levels`() {
                val match = createMatch(compLevel = CompLevel.SEMIFINAL)
                val result = match.getGroup(PlayoffType.BRACKET_8_TEAM)
                val compLevel = assertInstanceOf<MatchGroup.CompetitionLevel>(result)
                assertEquals(CompLevel.SEMIFINAL, compLevel.compLevel)
            }
        }
    }

    @Nested
    inner class GetDoubleElimRoundTests {
        // This tests the 2023+ double elimination format (8 team)
        @Test
        fun `setNumber 1-4 maps to Round 1`() {
            for (setNum in 1..4) {
                val match = createMatch(compLevel = CompLevel.SEMIFINAL, setNumber = setNum)
                val result = match.getGroup(PlayoffType.DOUBLE_ELIM_8_TEAM)
                val round = assertInstanceOf<MatchGroup.DoubleEliminationRound.Round>(result)
                assertEquals(1, round.number, "setNumber $setNum should map to Round 1")
            }
        }

        @Test
        fun `setNumber 5-8 maps to Round 2`() {
            for (setNum in 5..8) {
                val match = createMatch(compLevel = CompLevel.SEMIFINAL, setNumber = setNum)
                val result = match.getGroup(PlayoffType.DOUBLE_ELIM_8_TEAM)
                val round = assertInstanceOf<MatchGroup.DoubleEliminationRound.Round>(result)
                assertEquals(2, round.number, "setNumber $setNum should map to Round 2")
            }
        }

        @Test
        fun `setNumber 9-10 maps to Round 3`() {
            for (setNum in 9..10) {
                val match = createMatch(compLevel = CompLevel.SEMIFINAL, setNumber = setNum)
                val result = match.getGroup(PlayoffType.DOUBLE_ELIM_8_TEAM)
                val round = assertInstanceOf<MatchGroup.DoubleEliminationRound.Round>(result)
                assertEquals(3, round.number, "setNumber $setNum should map to Round 3")
            }
        }

        @Test
        fun `setNumber 11-12 maps to Round 4`() {
            for (setNum in 11..12) {
                val match = createMatch(compLevel = CompLevel.SEMIFINAL, setNumber = setNum)
                val result = match.getGroup(PlayoffType.DOUBLE_ELIM_8_TEAM)
                val round = assertInstanceOf<MatchGroup.DoubleEliminationRound.Round>(result)
                assertEquals(4, round.number, "setNumber $setNum should map to Round 4")
            }
        }

        @Test
        fun `setNumber 13 maps to Round 5`() {
            val match = createMatch(compLevel = CompLevel.SEMIFINAL, setNumber = 13)
            val result = match.getGroup(PlayoffType.DOUBLE_ELIM_8_TEAM)
            val round = assertInstanceOf<MatchGroup.DoubleEliminationRound.Round>(result)
            assertEquals(5, round.number)
        }

        @Test
        fun `setNumber 14+ maps to Unknown`() {
            for (setNum in 14..20) {
                val match = createMatch(compLevel = CompLevel.SEMIFINAL, setNumber = setNum)
                val result = match.getGroup(PlayoffType.DOUBLE_ELIM_8_TEAM)
                assertInstanceOf<MatchGroup.DoubleEliminationRound.Unknown>(result)
            }
        }

        @Test
        fun `FINAL comp level always returns Finals`() {
            for (setNum in 1..10) {
                val match = createMatch(compLevel = CompLevel.FINAL, setNumber = setNum)
                val result = match.getGroup(PlayoffType.DOUBLE_ELIM_8_TEAM)
                assertInstanceOf<MatchGroup.DoubleEliminationRound.Finals>(result)
            }
        }

        @Test
        fun `QUARTERFINAL in double elim 8 team returns Round based on setNumber`() {
            val match = createMatch(compLevel = CompLevel.QUARTERFINAL, setNumber = 1)
            val result = match.getGroup(PlayoffType.DOUBLE_ELIM_8_TEAM)
            val round = assertInstanceOf<MatchGroup.DoubleEliminationRound.Round>(result)
            assertEquals(1, round.number)
        }

        @Test
        fun `OCTOFINAL in double elim 8 team returns Round based on setNumber`() {
            val match = createMatch(compLevel = CompLevel.OCTOFINAL, setNumber = 1)
            val result = match.getGroup(PlayoffType.DOUBLE_ELIM_8_TEAM)
            val round = assertInstanceOf<MatchGroup.DoubleEliminationRound.Round>(result)
            assertEquals(1, round.number)
        }
    }

    @Nested
    inner class GetDoubleElim4RoundTests {
        // This tests the 4-team double elimination format (districts)
        @Test
        fun `setNumber 1-2 maps to Round 1`() {
            for (setNum in 1..2) {
                val match = createMatch(compLevel = CompLevel.SEMIFINAL, setNumber = setNum)
                val result = match.getGroup(PlayoffType.DOUBLE_ELIM_4_TEAM)
                val round = assertInstanceOf<MatchGroup.DoubleEliminationRound.Round>(result)
                assertEquals(1, round.number, "setNumber $setNum should map to Round 1")
            }
        }

        @Test
        fun `setNumber 3-4 maps to Round 2`() {
            for (setNum in 3..4) {
                val match = createMatch(compLevel = CompLevel.SEMIFINAL, setNumber = setNum)
                val result = match.getGroup(PlayoffType.DOUBLE_ELIM_4_TEAM)
                val round = assertInstanceOf<MatchGroup.DoubleEliminationRound.Round>(result)
                assertEquals(2, round.number, "setNumber $setNum should map to Round 2")
            }
        }

        @Test
        fun `setNumber 5 maps to Round 3`() {
            val match = createMatch(compLevel = CompLevel.SEMIFINAL, setNumber = 5)
            val result = match.getGroup(PlayoffType.DOUBLE_ELIM_4_TEAM)
            val round = assertInstanceOf<MatchGroup.DoubleEliminationRound.Round>(result)
            assertEquals(3, round.number)
        }

        @Test
        fun `setNumber 6+ maps to Unknown`() {
            for (setNum in 6..10) {
                val match = createMatch(compLevel = CompLevel.SEMIFINAL, setNumber = setNum)
                val result = match.getGroup(PlayoffType.DOUBLE_ELIM_4_TEAM)
                assertInstanceOf<MatchGroup.DoubleEliminationRound.Unknown>(result)
            }
        }

        @Test
        fun `FINAL comp level always returns Finals`() {
            for (setNum in 1..10) {
                val match = createMatch(compLevel = CompLevel.FINAL, setNumber = setNum)
                val result = match.getGroup(PlayoffType.DOUBLE_ELIM_4_TEAM)
                assertInstanceOf<MatchGroup.DoubleEliminationRound.Finals>(result)
            }
        }
    }

    @Nested
    inner class GetLegacyDoubleElimRoundTests {
        // This tests the pre-2023 double elimination format

        @Nested
        inner class OctoFinalTests {
            @Test
            fun `OCTOFINAL setNumber 1-4 maps to Round 1`() {
                for (setNum in 1..4) {
                    val match = createMatch(compLevel = CompLevel.OCTOFINAL, setNumber = setNum)
                    val result = match.getGroup(PlayoffType.LEGACY_DOUBLE_ELIM_8_TEAM)
                    val round = assertInstanceOf<MatchGroup.DoubleEliminationRound.Round>(result)
                    assertEquals(1, round.number, "OCTOFINAL setNumber $setNum should map to Round 1")
                }
            }

            @Test
            fun `OCTOFINAL setNumber 5-6 maps to Round 2`() {
                for (setNum in 5..6) {
                    val match = createMatch(compLevel = CompLevel.OCTOFINAL, setNumber = setNum)
                    val result = match.getGroup(PlayoffType.LEGACY_DOUBLE_ELIM_8_TEAM)
                    val round = assertInstanceOf<MatchGroup.DoubleEliminationRound.Round>(result)
                    assertEquals(2, round.number, "OCTOFINAL setNumber $setNum should map to Round 2")
                }
            }

            @Test
            fun `OCTOFINAL setNumber 7+ maps to Unknown`() {
                for (setNum in 7..10) {
                    val match = createMatch(compLevel = CompLevel.OCTOFINAL, setNumber = setNum)
                    val result = match.getGroup(PlayoffType.LEGACY_DOUBLE_ELIM_8_TEAM)
                    assertInstanceOf<MatchGroup.DoubleEliminationRound.Unknown>(result)
                }
            }
        }

        @Nested
        inner class QuarterFinalTests {
            @Test
            fun `QUARTERFINAL setNumber 1-2 maps to Round 2`() {
                for (setNum in 1..2) {
                    val match = createMatch(compLevel = CompLevel.QUARTERFINAL, setNumber = setNum)
                    val result = match.getGroup(PlayoffType.LEGACY_DOUBLE_ELIM_8_TEAM)
                    val round = assertInstanceOf<MatchGroup.DoubleEliminationRound.Round>(result)
                    assertEquals(2, round.number, "QUARTERFINAL setNumber $setNum should map to Round 2")
                }
            }

            @Test
            fun `QUARTERFINAL setNumber 3-4 maps to Round 3`() {
                for (setNum in 3..4) {
                    val match = createMatch(compLevel = CompLevel.QUARTERFINAL, setNumber = setNum)
                    val result = match.getGroup(PlayoffType.LEGACY_DOUBLE_ELIM_8_TEAM)
                    val round = assertInstanceOf<MatchGroup.DoubleEliminationRound.Round>(result)
                    assertEquals(3, round.number, "QUARTERFINAL setNumber $setNum should map to Round 3")
                }
            }

            @Test
            fun `QUARTERFINAL setNumber 5+ maps to Unknown`() {
                for (setNum in 5..10) {
                    val match = createMatch(compLevel = CompLevel.QUARTERFINAL, setNumber = setNum)
                    val result = match.getGroup(PlayoffType.LEGACY_DOUBLE_ELIM_8_TEAM)
                    assertInstanceOf<MatchGroup.DoubleEliminationRound.Unknown>(result)
                }
            }
        }

        @Nested
        inner class SemiFinalTests {
            @Test
            fun `SEMIFINAL always maps to Round 4`() {
                for (setNum in 1..10) {
                    val match = createMatch(compLevel = CompLevel.SEMIFINAL, setNumber = setNum)
                    val result = match.getGroup(PlayoffType.LEGACY_DOUBLE_ELIM_8_TEAM)
                    val round = assertInstanceOf<MatchGroup.DoubleEliminationRound.Round>(result)
                    assertEquals(4, round.number)
                }
            }
        }

        @Nested
        inner class FinalTests {
            @Test
            fun `FINAL setNumber 1 maps to Round 5`() {
                val match = createMatch(compLevel = CompLevel.FINAL, setNumber = 1)
                val result = match.getGroup(PlayoffType.LEGACY_DOUBLE_ELIM_8_TEAM)
                val round = assertInstanceOf<MatchGroup.DoubleEliminationRound.Round>(result)
                assertEquals(5, round.number)
            }

            @Test
            fun `FINAL setNumber 2 maps to Finals`() {
                val match = createMatch(compLevel = CompLevel.FINAL, setNumber = 2)
                val result = match.getGroup(PlayoffType.LEGACY_DOUBLE_ELIM_8_TEAM)
                assertInstanceOf<MatchGroup.DoubleEliminationRound.Finals>(result)
            }

            @Test
            fun `FINAL setNumber 3+ maps to Unknown`() {
                for (setNum in 3..10) {
                    val match = createMatch(compLevel = CompLevel.FINAL, setNumber = setNum)
                    val result = match.getGroup(PlayoffType.LEGACY_DOUBLE_ELIM_8_TEAM)
                    assertInstanceOf<MatchGroup.DoubleEliminationRound.Unknown>(result)
                }
            }
        }
    }

    @Nested
    inner class GetShortLabelTests {
        @Test
        fun `QUAL match label is Q followed by match number`() {
            val match = createMatch(compLevel = CompLevel.QUAL, matchNumber = 1)
            assertEquals("Q1", match.getShortLabel(PlayoffType.BRACKET_8_TEAM))

            val match2 = createMatch(compLevel = CompLevel.QUAL, matchNumber = 42)
            assertEquals("Q42", match2.getShortLabel(PlayoffType.BRACKET_8_TEAM))
        }

        @Test
        fun `OCTOFINAL match label is EF followed by set and match number`() {
            val match = createMatch(compLevel = CompLevel.OCTOFINAL, matchNumber = 1, setNumber = 2)
            assertEquals("EF2-1", match.getShortLabel(PlayoffType.BRACKET_8_TEAM))

            val match2 = createMatch(compLevel = CompLevel.OCTOFINAL, matchNumber = 3, setNumber = 4)
            assertEquals("EF4-3", match2.getShortLabel(PlayoffType.BRACKET_8_TEAM))
        }

        @Test
        fun `QUARTERFINAL match label is QF followed by set and match number`() {
            val match = createMatch(compLevel = CompLevel.QUARTERFINAL, matchNumber = 1, setNumber = 1)
            assertEquals("QF1-1", match.getShortLabel(PlayoffType.BRACKET_8_TEAM))

            val match2 = createMatch(compLevel = CompLevel.QUARTERFINAL, matchNumber = 2, setNumber = 3)
            assertEquals("QF3-2", match2.getShortLabel(PlayoffType.BRACKET_8_TEAM))
        }

        @Test
        fun `SEMIFINAL match label is SF followed by set and match number`() {
            val match = createMatch(compLevel = CompLevel.SEMIFINAL, matchNumber = 1, setNumber = 1)
            assertEquals("SF1-1", match.getShortLabel(PlayoffType.BRACKET_8_TEAM))

            val match2 = createMatch(compLevel = CompLevel.SEMIFINAL, matchNumber = 2, setNumber = 2)
            assertEquals("SF2-2", match2.getShortLabel(PlayoffType.BRACKET_8_TEAM))
        }

        @Test
        fun `FINAL match label is F followed by set and match number`() {
            val match = createMatch(compLevel = CompLevel.FINAL, matchNumber = 1, setNumber = 1)
            assertEquals("F1-1", match.getShortLabel(PlayoffType.BRACKET_8_TEAM))

            val match2 = createMatch(compLevel = CompLevel.FINAL, matchNumber = 2, setNumber = 3)
            assertEquals("F3-2", match2.getShortLabel(PlayoffType.BRACKET_8_TEAM))
        }

        @Test
        fun `Double elim round label is R followed by round and set number`() {
            val match = createMatch(compLevel = CompLevel.SEMIFINAL, matchNumber = 1, setNumber = 1)
            assertEquals("R1-1", match.getShortLabel(PlayoffType.DOUBLE_ELIM_8_TEAM))

            val match2 = createMatch(compLevel = CompLevel.SEMIFINAL, matchNumber = 1, setNumber = 5)
            assertEquals("R2-5", match2.getShortLabel(PlayoffType.DOUBLE_ELIM_8_TEAM))

            val match3 = createMatch(compLevel = CompLevel.SEMIFINAL, matchNumber = 1, setNumber = 13)
            assertEquals("R5-13", match3.getShortLabel(PlayoffType.DOUBLE_ELIM_8_TEAM))
        }

        @Test
        fun `Double elim finals label is F followed by match number`() {
            val match = createMatch(compLevel = CompLevel.FINAL, matchNumber = 1, setNumber = 1)
            assertEquals("F-1", match.getShortLabel(PlayoffType.DOUBLE_ELIM_8_TEAM))

            val match2 = createMatch(compLevel = CompLevel.FINAL, matchNumber = 2, setNumber = 2)
            assertEquals("F-2", match2.getShortLabel(PlayoffType.DOUBLE_ELIM_8_TEAM))
        }

        @Test
        fun `Unknown double elim round uses comp level label`() {
            val match = createMatch(compLevel = CompLevel.SEMIFINAL, matchNumber = 1, setNumber = 20)
            assertEquals("SF20-1", match.getShortLabel(PlayoffType.DOUBLE_ELIM_8_TEAM))
        }

        @Test
        fun `OTHER comp level short label is just the set and match number`() {
            val match = createMatch(compLevel = CompLevel.OTHER, matchNumber = 1, setNumber = 1)
            assertEquals("1-1", match.getShortLabel(PlayoffType.BRACKET_8_TEAM))
        }
    }

    @Nested
    inner class GetFullLabelTests {
        @Test
        fun `QUAL match full label is Qual followed by match number`() {
            val match = createMatch(compLevel = CompLevel.QUAL, matchNumber = 1)
            assertEquals("Qual 1", match.getFullLabel(PlayoffType.BRACKET_8_TEAM))

            val match2 = createMatch(compLevel = CompLevel.QUAL, matchNumber = 42)
            assertEquals("Qual 42", match2.getFullLabel(PlayoffType.BRACKET_8_TEAM))
        }

        @Test
        fun `OCTOFINAL match full label is Eights followed by set and match number`() {
            val match = createMatch(compLevel = CompLevel.OCTOFINAL, matchNumber = 1, setNumber = 2)
            assertEquals("Eights 2-1", match.getFullLabel(PlayoffType.BRACKET_8_TEAM))

            val match2 = createMatch(compLevel = CompLevel.OCTOFINAL, matchNumber = 3, setNumber = 4)
            assertEquals("Eights 4-3", match2.getFullLabel(PlayoffType.BRACKET_8_TEAM))
        }

        @Test
        fun `QUARTERFINAL match full label is Quarters followed by set and match number`() {
            val match = createMatch(compLevel = CompLevel.QUARTERFINAL, matchNumber = 1, setNumber = 1)
            assertEquals("Quarters 1-1", match.getFullLabel(PlayoffType.BRACKET_8_TEAM))

            val match2 = createMatch(compLevel = CompLevel.QUARTERFINAL, matchNumber = 2, setNumber = 3)
            assertEquals("Quarters 3-2", match2.getFullLabel(PlayoffType.BRACKET_8_TEAM))
        }

        @Test
        fun `SEMIFINAL match full label is Semis followed by set and match number`() {
            val match = createMatch(compLevel = CompLevel.SEMIFINAL, matchNumber = 1, setNumber = 1)
            assertEquals("Semis 1-1", match.getFullLabel(PlayoffType.BRACKET_8_TEAM))

            val match2 = createMatch(compLevel = CompLevel.SEMIFINAL, matchNumber = 2, setNumber = 2)
            assertEquals("Semis 2-2", match2.getFullLabel(PlayoffType.BRACKET_8_TEAM))
        }

        @Test
        fun `FINAL match full label is Final followed by set and match number`() {
            val match = createMatch(compLevel = CompLevel.FINAL, matchNumber = 1, setNumber = 1)
            assertEquals("Final 1-1", match.getFullLabel(PlayoffType.BRACKET_8_TEAM))

            val match2 = createMatch(compLevel = CompLevel.FINAL, matchNumber = 2, setNumber = 3)
            assertEquals("Final 3-2", match2.getFullLabel(PlayoffType.BRACKET_8_TEAM))
        }

        @Test
        fun `Double elim round full label is Match setNumber in parentheses with round`() {
            val match = createMatch(compLevel = CompLevel.SEMIFINAL, matchNumber = 1, setNumber = 1)
            assertEquals("Match 1 (Round 1)", match.getFullLabel(PlayoffType.DOUBLE_ELIM_8_TEAM))

            val match2 = createMatch(compLevel = CompLevel.SEMIFINAL, matchNumber = 1, setNumber = 5)
            assertEquals("Match 5 (Round 2)", match2.getFullLabel(PlayoffType.DOUBLE_ELIM_8_TEAM))

            val match3 = createMatch(compLevel = CompLevel.SEMIFINAL, matchNumber = 1, setNumber = 13)
            assertEquals("Match 13 (Round 5)", match3.getFullLabel(PlayoffType.DOUBLE_ELIM_8_TEAM))
        }

        @Test
        fun `Double elim finals full label is Final followed by match number`() {
            val match = createMatch(compLevel = CompLevel.FINAL, matchNumber = 1, setNumber = 1)
            assertEquals("Final 1", match.getFullLabel(PlayoffType.DOUBLE_ELIM_8_TEAM))

            val match2 = createMatch(compLevel = CompLevel.FINAL, matchNumber = 2, setNumber = 2)
            assertEquals("Final 2", match2.getFullLabel(PlayoffType.DOUBLE_ELIM_8_TEAM))
        }

        @Test
        fun `Unknown double elim round uses comp level full label`() {
            val match = createMatch(compLevel = CompLevel.SEMIFINAL, matchNumber = 1, setNumber = 20)
            assertEquals("Semis 20-1", match.getFullLabel(PlayoffType.DOUBLE_ELIM_8_TEAM))
        }

        @Test
        fun `OTHER comp level full label is just the set and match number`() {
            val match = createMatch(compLevel = CompLevel.OTHER, matchNumber = 1, setNumber = 1)
            assertEquals("1-1", match.getFullLabel(PlayoffType.BRACKET_8_TEAM))
        }
    }

    @Nested
    inner class EdgeCasesTests {
        @Test
        fun `zero match number`() {
            val match = createMatch(compLevel = CompLevel.QUAL, matchNumber = 0)
            assertEquals("Q0", match.getShortLabel(PlayoffType.BRACKET_8_TEAM))
            assertEquals("Qual 0", match.getFullLabel(PlayoffType.BRACKET_8_TEAM))
        }

        @Test
        fun `zero set number`() {
            val match = createMatch(compLevel = CompLevel.QUARTERFINAL, matchNumber = 1, setNumber = 0)
            assertEquals("QF0-1", match.getShortLabel(PlayoffType.BRACKET_8_TEAM))
            assertEquals("Quarters 0-1", match.getFullLabel(PlayoffType.BRACKET_8_TEAM))
        }

        @Test
        fun `very large match number`() {
            val match = createMatch(compLevel = CompLevel.QUAL, matchNumber = 999)
            assertEquals("Q999", match.getShortLabel(PlayoffType.BRACKET_8_TEAM))
            assertEquals("Qual 999", match.getFullLabel(PlayoffType.BRACKET_8_TEAM))
        }

        @Test
        fun `very large set number in double elim`() {
            val match = createMatch(compLevel = CompLevel.SEMIFINAL, matchNumber = 1, setNumber = 1000)
            val result = match.getGroup(PlayoffType.DOUBLE_ELIM_8_TEAM)
            assertInstanceOf<MatchGroup.DoubleEliminationRound.Unknown>(result)
            assertEquals("SF1000-1", match.getShortLabel(PlayoffType.DOUBLE_ELIM_8_TEAM))
        }

        @Test
        fun `all non-playoff comp levels return CompetitionLevel with any playoff type`() {
            for (playoffType in listOf(
                PlayoffType.BRACKET_8_TEAM,
                PlayoffType.BRACKET_4_TEAM,
                PlayoffType.ROUND_ROBIN_6_TEAM,
                PlayoffType.OTHER,
            )) {
                val match = createMatch(compLevel = CompLevel.QUAL)
                val result = match.getGroup(playoffType)
                val compLevel = assertInstanceOf<MatchGroup.CompetitionLevel>(result)
                assertEquals(CompLevel.QUAL, compLevel.compLevel)
            }
        }

        @Test
        fun `double elim 4 team with edge set numbers`() {
            // Test boundaries
            val match1 = createMatch(compLevel = CompLevel.SEMIFINAL, setNumber = 1)
            val result1 = match1.getGroup(PlayoffType.DOUBLE_ELIM_4_TEAM)
            val round1 = assertInstanceOf<MatchGroup.DoubleEliminationRound.Round>(result1)
            assertEquals(1, round1.number)

            val match2 = createMatch(compLevel = CompLevel.SEMIFINAL, setNumber = 2)
            val result2 = match2.getGroup(PlayoffType.DOUBLE_ELIM_4_TEAM)
            val round2 = assertInstanceOf<MatchGroup.DoubleEliminationRound.Round>(result2)
            assertEquals(1, round2.number)

            val match3 = createMatch(compLevel = CompLevel.SEMIFINAL, setNumber = 3)
            val result3 = match3.getGroup(PlayoffType.DOUBLE_ELIM_4_TEAM)
            val round3 = assertInstanceOf<MatchGroup.DoubleEliminationRound.Round>(result3)
            assertEquals(2, round3.number)

            val match4 = createMatch(compLevel = CompLevel.SEMIFINAL, setNumber = 5)
            val result4 = match4.getGroup(PlayoffType.DOUBLE_ELIM_4_TEAM)
            val round4 = assertInstanceOf<MatchGroup.DoubleEliminationRound.Round>(result4)
            assertEquals(3, round4.number)

            val match5 = createMatch(compLevel = CompLevel.SEMIFINAL, setNumber = 6)
            val result5 = match5.getGroup(PlayoffType.DOUBLE_ELIM_4_TEAM)
            assertInstanceOf<MatchGroup.DoubleEliminationRound.Unknown>(result5)
        }

        @Test
        fun `legacy double elim octo boundary cases`() {
            val match1 = createMatch(compLevel = CompLevel.OCTOFINAL, setNumber = 4)
            val result1 = match1.getGroup(PlayoffType.LEGACY_DOUBLE_ELIM_8_TEAM)
            val round1 = assertInstanceOf<MatchGroup.DoubleEliminationRound.Round>(result1)
            assertEquals(1, round1.number)

            val match2 = createMatch(compLevel = CompLevel.OCTOFINAL, setNumber = 5)
            val result2 = match2.getGroup(PlayoffType.LEGACY_DOUBLE_ELIM_8_TEAM)
            val round2 = assertInstanceOf<MatchGroup.DoubleEliminationRound.Round>(result2)
            assertEquals(2, round2.number)

            val match3 = createMatch(compLevel = CompLevel.OCTOFINAL, setNumber = 6)
            val result3 = match3.getGroup(PlayoffType.LEGACY_DOUBLE_ELIM_8_TEAM)
            val round3 = assertInstanceOf<MatchGroup.DoubleEliminationRound.Round>(result3)
            assertEquals(2, round3.number)

            val match4 = createMatch(compLevel = CompLevel.OCTOFINAL, setNumber = 7)
            val result4 = match4.getGroup(PlayoffType.LEGACY_DOUBLE_ELIM_8_TEAM)
            assertInstanceOf<MatchGroup.DoubleEliminationRound.Unknown>(result4)
        }
    }

    @Nested
    inner class RegressionTests {
        @Test
        fun `double elim 8 team round 5 boundary is exactly at setNumber 13`() {
            val match12 = createMatch(compLevel = CompLevel.SEMIFINAL, setNumber = 12)
            val result12 = match12.getGroup(PlayoffType.DOUBLE_ELIM_8_TEAM)
            val round12 = assertInstanceOf<MatchGroup.DoubleEliminationRound.Round>(result12)
            assertEquals(4, round12.number)

            val match13 = createMatch(compLevel = CompLevel.SEMIFINAL, setNumber = 13)
            val result13 = match13.getGroup(PlayoffType.DOUBLE_ELIM_8_TEAM)
            val round13 = assertInstanceOf<MatchGroup.DoubleEliminationRound.Round>(result13)
            assertEquals(5, round13.number)

            val match14 = createMatch(compLevel = CompLevel.SEMIFINAL, setNumber = 14)
            val result14 = match14.getGroup(PlayoffType.DOUBLE_ELIM_8_TEAM)
            assertInstanceOf<MatchGroup.DoubleEliminationRound.Unknown>(result14)
        }

        @Test
        fun `legacy double elim final set number 2 is finals not round 5`() {
            val match1 = createMatch(compLevel = CompLevel.FINAL, setNumber = 1)
            val result1 = match1.getGroup(PlayoffType.LEGACY_DOUBLE_ELIM_8_TEAM)
            val round1 = assertInstanceOf<MatchGroup.DoubleEliminationRound.Round>(result1)
            assertEquals(5, round1.number)

            val match2 = createMatch(compLevel = CompLevel.FINAL, setNumber = 2)
            val result2 = match2.getGroup(PlayoffType.LEGACY_DOUBLE_ELIM_8_TEAM)
            assertInstanceOf<MatchGroup.DoubleEliminationRound.Finals>(result2)

            val match3 = createMatch(compLevel = CompLevel.FINAL, setNumber = 3)
            val result3 = match3.getGroup(PlayoffType.LEGACY_DOUBLE_ELIM_8_TEAM)
            assertInstanceOf<MatchGroup.DoubleEliminationRound.Unknown>(result3)
        }
    }
}
