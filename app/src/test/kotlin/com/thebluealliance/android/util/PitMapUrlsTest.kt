package com.thebluealliance.android.util

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class PitMapUrlsTest {
    @Test
    fun `build tba pit map url with no teams`() {
        assertEquals(
            "https://www.thebluealliance.com/event/2026miket/pitmap",
            buildTbaPitMapUrl("2026miket"),
        )
    }

    @Test
    fun `build tba pit map url with filtered teams`() {
        assertEquals(
            "https://www.thebluealliance.com/event/2026miket/pitmap?teams=frc33,frc67",
            buildTbaPitMapUrl("2026miket", listOf(" frc33 ", "frc67", "67", "frc33")),
        )
    }

    @Test
    fun `build nexus pit map url for event`() {
        assertEquals(
            "https://frc.nexus/en/event/2026miket/map",
            buildNexusPitMapUrl("2026miket"),
        )
    }

    @Test
    fun `build nexus pit map url for team highlight`() {
        assertEquals(
            "https://frc.nexus/en/event/2026miket/team/254/map",
            buildNexusPitMapUrl("2026miket", "frc254"),
        )
    }
}
