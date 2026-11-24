package com.thebluealliance.android.ui.common

import app.cash.turbine.test
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class UserMessageBusTest {
    @Test
    fun `holds messages sent before anyone collects`() =
        runTest {
            val bus = UserMessageBus()
            bus.show("first")
            bus.show("second")
            bus.messages.test {
                assertEquals("first", awaitItem())
                assertEquals("second", awaitItem())
            }
        }

    @Test
    fun `delivers each message once`() =
        runTest {
            val bus = UserMessageBus()
            bus.show("once")
            bus.messages.test {
                assertEquals("once", awaitItem())
                expectNoEvents()
            }
            bus.messages.test { expectNoEvents() }
        }
}
