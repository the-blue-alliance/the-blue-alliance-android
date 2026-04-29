package com.thebluealliance.android.util

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class PitMapUrlsTest {
    // ── buildTbaPitMapUrl ────────────────────────────────────────────────────

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

    // ── buildNexusPitMapUrl ──────────────────────────────────────────────────

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

    @Test
    fun `build nexus pit map url ignores blank team key`() {
        assertEquals(
            "https://frc.nexus/en/event/2026miket/map",
            buildNexusPitMapUrl("2026miket", "frc"),
        )
    }

    // ── buildNexusEventCode ──────────────────────────────────────────────────

    @Test
    fun `build nexus event code falls back to event key suffix for regular event`() {
        assertEquals("2026MIKET", buildNexusEventCode("2026miket", 2026, null))
    }

    @Test
    fun `build nexus event code uses first_event_code for cmp division`() {
        // TBA key "2026cmptxcur" maps to FIRST code "CUR" via firstEventCode
        assertEquals("2026CUR", buildNexusEventCode("2026cmptxcur", 2026, "CUR"))
    }

    @Test
    fun `build nexus event code uppercases first_event_code`() {
        assertEquals("2026CUR", buildNexusEventCode("2026cmptxcur", 2026, "cur"))
    }

    @Test
    fun `build nexus event code ignores blank first_event_code`() {
        assertEquals("2026MIKET", buildNexusEventCode("2026miket", 2026, "   "))
    }

    @Test
    fun `build nexus event code ignores empty first_event_code`() {
        assertEquals("2026MIKET", buildNexusEventCode("2026miket", 2026, ""))
    }
}
