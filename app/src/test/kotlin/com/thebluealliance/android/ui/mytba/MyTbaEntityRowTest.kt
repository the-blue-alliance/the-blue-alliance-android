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
}
