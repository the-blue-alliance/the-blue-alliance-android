package com.thebluealliance.android.navigation

import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcher
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class KeyboardBackTest {
    private var leftApp = 0
    private val dispatcher = OnBackPressedDispatcher { leftApp++ }

    private fun callback(enabled: Boolean): CountingCallback =
        CountingCallback(enabled).also { dispatcher.addCallback(it) }

    @Test
    fun `nothing to go back to does nothing`() {
        assertFalse(dispatcher.keyboardBack())
        assertEquals(0, leftApp)
    }

    @Test
    fun `a disabled in-app handler does not leave the app either`() {
        val handler = callback(enabled = false)
        assertFalse(dispatcher.keyboardBack())
        assertEquals(0, handler.calls)
        assertEquals(0, leftApp)
    }

    @Test
    fun `an enabled in-app handler goes back`() {
        val handler = callback(enabled = true)
        assertTrue(dispatcher.keyboardBack())
        assertEquals(1, handler.calls)
        assertEquals(0, leftApp)
    }

    @Test
    fun `system back still leaves the app with nothing to go back to`() {
        dispatcher.onBackPressed()
        assertEquals(1, leftApp)
    }

    private class CountingCallback(
        enabled: Boolean,
    ) : OnBackPressedCallback(enabled) {
        var calls = 0

        override fun handleOnBackPressed() {
            calls++
        }
    }
}
