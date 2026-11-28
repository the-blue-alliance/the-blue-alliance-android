package com.thebluealliance.android.ui.events.detail.tabs

import com.thebluealliance.android.domain.model.EventCOPRs
import com.thebluealliance.android.domain.model.EventInsights
import com.thebluealliance.android.domain.model.EventOPRs
import com.thebluealliance.android.ui.components.isCurrentSection
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class EventInsightsTabLogicTest {
    @Test
    fun `opr row description labels each column`() {
        assertEquals(
            "Team 33, OPR 71.09, DPR 17.28, CCWM 53.81",
            oprRowDescription("33", 71.0912, 17.2777, 53.81),
        )
    }

    @Test
    fun `opr row description keeps sign and two decimals`() {
        assertEquals(
            "Team 9999, OPR 0.00, DPR 5.50, CCWM -5.50",
            oprRowDescription("9999", 0.0, 5.5, -5.5),
        )
    }

    @Test
    fun `copr row description names the selected stat`() {
        assertEquals(
            "Team 254, Total Auto Points 12.35",
            coprRowDescription("254", "Total Auto Points", 12.345678),
        )
    }

    @Test
    fun `team cells name the team they open by number`() {
        assertEquals("View team 254", teamCellClickLabel("frc254"))
    }

    private val coprStats = listOf("Hub Second Active Shift Count", "autoTowerPoints")

    @Test
    fun `menu offers the three views then each component opr`() {
        assertEquals(
            listOf(
                StatType.StandardOPRs,
                StatType.QualInsights,
                StatType.PlayoffInsights,
                StatType.COPR("Hub Second Active Shift Count"),
                StatType.COPR("autoTowerPoints"),
            ),
            statTypeOptions(coprStats),
        )
    }

    @Test
    fun `menu offers the three views when there are no component oprs`() {
        assertEquals(
            listOf(StatType.StandardOPRs, StatType.QualInsights, StatType.PlayoffInsights),
            statTypeOptions(emptyList()),
        )
    }

    @Test
    fun `view labels are sentence case and component oprs keep their names`() {
        assertEquals(
            listOf(
                "OPRs",
                "Qual insights",
                "Playoff insights",
                "Hub Second Active Shift Count",
                "autoTowerPoints",
            ),
            statTypeMenuEntries(statTypeOptions(coprStats)).map { it.label },
        )
    }

    @Test
    fun `menu marks only the current view`() {
        val entries = statTypeMenuEntries(statTypeOptions(coprStats))
        val current = statTypeLabel(StatType.COPR("autoTowerPoints"))
        assertEquals(
            listOf("autoTowerPoints"),
            entries
                .filter {
                    isCurrentSection(it, current)
                }.map { it.label },
        )
    }

    @Test
    fun `picking an entry selects its view`() {
        val options = statTypeOptions(coprStats)
        val entries = statTypeMenuEntries(options)
        options.forEachIndexed {
            index,
            type,
            ->
            assertEquals(type, statTypeForEntry(options, entries[index]))
        }
    }

    @Test
    fun `tab opens on oprs when there are any`() {
        assertEquals(
            StatType.StandardOPRs,
            defaultStatType(
                EventOPRs(oprs = mapOf("frc254" to 1.0)),
                EventCOPRs(mapOf("autoTowerPoints" to mapOf("frc254" to 1.0))),
                EventInsights(qual = "{}"),
            ),
        )
    }

    @Test
    fun `tab opens on qual insights without oprs`() {
        assertEquals(
            StatType.QualInsights,
            defaultStatType(EventOPRs(), null, EventInsights("{}", "{}")),
        )
    }

    @Test
    fun `tab opens on playoff insights without oprs or qual insights`() {
        assertEquals(
            StatType.PlayoffInsights,
            defaultStatType(null, null, EventInsights(playoff = "{}")),
        )
    }

    @Test
    fun `tab opens on the first component opr with nothing else`() {
        assertEquals(
            StatType.COPR("autoTowerPoints"),
            defaultStatType(
                null,
                EventCOPRs(mapOf("autoTowerPoints" to mapOf("frc254" to 1.0))),
                null,
            ),
        )
    }

    @Test
    fun `tab opens on oprs with no data at all`() {
        assertEquals(StatType.StandardOPRs, defaultStatType(null, null, null))
    }
}
