package com.thebluealliance.android.ui.mytba

import com.thebluealliance.android.domain.model.ModelType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class MyTbaEntityRowTest {
    @Test
    fun `labels each model type`() {
        assertEquals("Event", modelTypeLabel(ModelType.EVENT))
        assertEquals("Team", modelTypeLabel(ModelType.TEAM))
        assertEquals("Match", modelTypeLabel(ModelType.MATCH))
        assertEquals("Team @ Event", modelTypeLabel(ModelType.EVENT_TEAM))
        assertEquals("District", modelTypeLabel(ModelType.DISTRICT))
    }

    @Test
    fun `labels unknown model types as other`() {
        assertEquals("Other", modelTypeLabel(-1))
    }

    @Test
    fun `click labels name the destination`() {
        assertEquals("View team", modelTypeClickLabel(ModelType.TEAM))
        assertEquals("View event", modelTypeClickLabel(ModelType.EVENT))
        assertEquals("View match", modelTypeClickLabel(ModelType.MATCH))
    }

    @Test
    fun `team at event rows open Team @ Event`() {
        assertEquals("View team at event", modelTypeClickLabel(ModelType.EVENT_TEAM))
    }

    @Test
    fun `rows without a destination keep the default click label`() {
        assertEquals(null, modelTypeClickLabel(ModelType.DISTRICT))
    }

    private fun openRow(
        modelType: Int,
        modelKey: String,
    ): String {
        var opened = ""
        MyTbaRowNavigation(
            toTeam = { opened = "team $it" },
            toEvent = { opened = "event $it" },
            toMatch = { opened = "match $it" },
            toTeamAtEvent = { team, event -> opened = "team $team at $event" },
        ).open(modelType, modelKey)
        return opened
    }

    @Test
    fun `team at event rows navigate to Team @ Event`() {
        assertEquals(
            "team frc2471 at 2024micmp4",
            openRow(ModelType.EVENT_TEAM, "2024micmp4_frc2471"),
        )
    }

    @Test
    fun `other rows navigate to their model`() {
        assertEquals("team frc254", openRow(ModelType.TEAM, "frc254"))
        assertEquals("event 2024casj", openRow(ModelType.EVENT, "2024casj"))
        assertEquals("match 2024casj_qm1", openRow(ModelType.MATCH, "2024casj_qm1"))
    }

    @Test
    fun `a malformed team at event key goes nowhere`() {
        assertEquals("", openRow(ModelType.EVENT_TEAM, "2024micmp4"))
        assertEquals("", openRow(ModelType.EVENT_TEAM, "_frc2471"))
        assertEquals("", openRow(ModelType.EVENT_TEAM, "2024micmp4_"))
    }
}
