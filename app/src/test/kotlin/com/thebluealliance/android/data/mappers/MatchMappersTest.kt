package com.thebluealliance.android.data.mappers

import com.thebluealliance.android.data.remote.dto.MatchAllianceDto
import com.thebluealliance.android.data.remote.dto.MatchAlliancesDto
import com.thebluealliance.android.data.remote.dto.MatchDto
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class MatchMappersTest {
    private fun matchDto(alliances: MatchAlliancesDto?) =
        MatchDto(
            key = "2026test_qm10",
            eventKey = "2026test",
            compLevel = "qm",
            matchNumber = 10,
            setNumber = 1,
            alliances = alliances,
            time = 1750000000,
            winningAlliance = "red",
        )

    @Test
    fun `dto to entity to domain round-trips team and surrogate team keys`() {
        val dto =
            matchDto(
                MatchAlliancesDto(
                    red =
                        MatchAllianceDto(
                            score = 95,
                            teamKeys = listOf("frc254", "frc1323", "frc973"),
                            surrogateTeamKeys = listOf("frc254"),
                        ),
                    blue =
                        MatchAllianceDto(
                            score = 80,
                            teamKeys = listOf("frc1678", "frc118", "frc2056"),
                            surrogateTeamKeys = listOf("frc118", "frc2056"),
                        ),
                ),
            )

        val domain = dto.toEntity().toDomain()

        assertEquals(listOf("frc254", "frc1323", "frc973"), domain.redTeamKeys)
        assertEquals(listOf("frc254"), domain.redSurrogateTeamKeys)
        assertEquals(listOf("frc1678", "frc118", "frc2056"), domain.blueTeamKeys)
        assertEquals(listOf("frc118", "frc2056"), domain.blueSurrogateTeamKeys)
        assertEquals(95, domain.redScore)
        assertEquals(80, domain.blueScore)
    }

    @Test
    fun `alliances without surrogates round-trip to empty surrogate lists`() {
        val dto =
            matchDto(
                MatchAlliancesDto(
                    red = MatchAllianceDto(score = 95, teamKeys = listOf("frc1", "frc2", "frc3")),
                    blue = MatchAllianceDto(score = 80, teamKeys = listOf("frc4", "frc5", "frc6")),
                ),
            )

        val entity = dto.toEntity()
        val domain = entity.toDomain()

        assertEquals("", entity.redSurrogateTeamKeys)
        assertEquals("", entity.blueSurrogateTeamKeys)
        assertEquals(emptyList<String>(), domain.redSurrogateTeamKeys)
        assertEquals(emptyList<String>(), domain.blueSurrogateTeamKeys)
    }

    @Test
    fun `missing alliances round-trip to empty team and surrogate lists`() {
        val domain = matchDto(alliances = null).toEntity().toDomain()

        assertEquals(emptyList<String>(), domain.redTeamKeys)
        assertEquals(emptyList<String>(), domain.redSurrogateTeamKeys)
        assertEquals(emptyList<String>(), domain.blueTeamKeys)
        assertEquals(emptyList<String>(), domain.blueSurrogateTeamKeys)
    }
}
