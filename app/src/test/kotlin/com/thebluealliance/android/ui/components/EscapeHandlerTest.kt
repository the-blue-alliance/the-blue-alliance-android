package com.thebluealliance.android.ui.components

import android.view.KeyEvent
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class EscapeHandlerTest {
    private val calls = mutableListOf<String>()
    private val dispatcher = EscapeDispatcher()

    private fun handler(name: String): () -> Unit = { calls += name }

    private fun key(
        action: Int,
        keyCode: Int = KeyEvent.KEYCODE_ESCAPE,
        repeatCount: Int = 0,
        isCanceled: Boolean = false,
    ) = dispatcher.onKey(keyCode, action, repeatCount, isCanceled)

    private fun press() = key(KeyEvent.ACTION_DOWN) to key(KeyEvent.ACTION_UP)

    @Test
    fun `Esc acts on release and claims the press`() {
        dispatcher.add(handler("pane"))
        assertTrue(key(KeyEvent.ACTION_DOWN))
        assertEquals(emptyList<String>(), calls)
        assertTrue(key(KeyEvent.ACTION_UP))
        assertEquals(listOf("pane"), calls)
    }

    @Test
    fun `a release without its press does nothing`() {
        dispatcher.add(handler("pane"))
        assertFalse(key(KeyEvent.ACTION_UP))
        assertEquals(emptyList<String>(), calls)
    }

    @Test
    fun `a canceled release does nothing`() {
        dispatcher.add(handler("pane"))
        key(KeyEvent.ACTION_DOWN)
        key(KeyEvent.ACTION_UP, isCanceled = true)
        assertEquals(emptyList<String>(), calls)
    }

    @Test
    fun `a held key acts once`() {
        dispatcher.add(handler("pane"))
        key(KeyEvent.ACTION_DOWN)
        key(KeyEvent.ACTION_DOWN, repeatCount = 1)
        key(KeyEvent.ACTION_UP)
        key(KeyEvent.ACTION_UP)
        assertEquals(listOf("pane"), calls)
    }

    @Test
    fun `the newest handler wins, and the older one runs once it is gone`() {
        val screen = handler("screen")
        val pane = handler("pane")
        dispatcher.add(screen)
        dispatcher.add(pane)
        press()
        assertEquals(listOf("pane"), calls)
        dispatcher.remove(pane)
        press()
        assertEquals(listOf("pane", "screen"), calls)
    }

    @Test
    fun `other keys and an empty dispatcher pass through`() {
        assertFalse(key(KeyEvent.ACTION_DOWN))
        dispatcher.add(handler("pane"))
        assertFalse(key(KeyEvent.ACTION_UP, keyCode = KeyEvent.KEYCODE_BACK))
        assertFalse(key(KeyEvent.ACTION_DOWN, keyCode = KeyEvent.KEYCODE_ENTER))
        assertEquals(emptyList<String>(), calls)
    }
}
