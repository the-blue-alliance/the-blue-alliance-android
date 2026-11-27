package com.thebluealliance.android.wear.tracker

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class AutoRefreshTest {
    @Test
    fun `refreshes a tracked team`() {
        assertTrue(AutoRefresh.allowed(teamNumber = "254", teamNotFound = false))
    }

    @Test
    fun `skips when no team is tracked`() {
        assertFalse(AutoRefresh.allowed(teamNumber = "", teamNotFound = false))
    }

    @Test
    fun `skips a team the API said doesn't exist`() {
        assertFalse(AutoRefresh.allowed(teamNumber = "99999", teamNotFound = true))
    }
}
