package com.thebluealliance.android.ui.common

import app.cash.turbine.test
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class UserMessageBusTest {
    @Test
    fun `holds messages sent before anyone collects`() =
        runTest {
            val bus = UserMessageBus()
            bus.show("first")
            bus.show("second")
            bus.messages.test {
                assertEquals("first", awaitItem().text)
                assertEquals("second", awaitItem().text)
            }
        }

    @Test
    fun `delivers each message once`() =
        runTest {
            val bus = UserMessageBus()
            bus.show("once")
            bus.messages.test {
                assertEquals("once", awaitItem().text)
                expectNoEvents()
            }
            bus.messages.test { expectNoEvents() }
        }

    @Test
    fun `a Retry message carries its action`() =
        runTest {
            val bus = UserMessageBus()
            var retried = 0
            bus.showRetry("Couldn't refresh") { retried++ }
            bus.messages.test {
                val message = awaitItem()
                assertEquals("Couldn't refresh", message.text)
                assertEquals("Retry", message.action?.label)
                message.action!!.onPerform()
                assertEquals(1, retried)
            }
        }

    @Test
    fun `a plain message has no action`() =
        runTest {
            val bus = UserMessageBus()
            bus.show("Saved")
            bus.messages.test { assertNull(awaitItem().action) }
        }
}
