package com.thebluealliance.android.ui.components

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class RefreshTargetsTest {
    private val refreshed = mutableListOf<String>()
    private val targets = RefreshTargets()

    @Test
    fun `no screen to refresh does nothing`() {
        assertFalse(targets.refresh())
    }

    @Test
    fun `refreshes the newest screen`() {
        targets.register { refreshed += "events" }
        targets.register { refreshed += "event detail" }

        assertTrue(targets.refresh())

        assertEquals(listOf("event detail"), refreshed)
    }

    @Test
    fun `falls back to the older screen once the newer one leaves`() {
        targets.register { refreshed += "event list" }
        val unregisterMatch = targets.register { refreshed += "match" }

        unregisterMatch()
        targets.refresh()

        assertEquals(listOf("event list"), refreshed)
    }

    @Test
    fun `an outgoing screen leaving keeps the incoming one`() {
        val unregisterOld = targets.register { refreshed += "old" }
        targets.register { refreshed += "new" }

        unregisterOld()
        targets.refresh()

        assertEquals(listOf("new"), refreshed)
    }

    @Test
    fun `nothing is left once every screen leaves`() {
        val unregister = targets.register { refreshed += "teams" }

        unregister()

        assertFalse(targets.refresh())
        assertEquals(emptyList<String>(), refreshed)
    }
}
