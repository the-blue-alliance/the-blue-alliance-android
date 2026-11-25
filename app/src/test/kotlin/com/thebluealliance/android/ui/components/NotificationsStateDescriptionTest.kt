package com.thebluealliance.android.ui.components

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class NotificationsStateDescriptionTest {
    @Test
    fun `bell announces whether notifications are on`() {
        assertEquals("Notifications on", notificationsStateDescription(notificationsOn = true))
        assertEquals("Notifications off", notificationsStateDescription(notificationsOn = false))
    }
}
