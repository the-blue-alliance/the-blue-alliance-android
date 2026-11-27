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
}
