package com.thebluealliance.android.ui.components

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class ScrollbarTest {
    @Test
    fun `no thumb when the content fits`() {
        assertNull(scrollbarThumb(0, 800, 800, 800f, 48f))
        assertNull(scrollbarThumb(0, 500, 800, 800f, 48f))
    }

    @Test
    fun `no thumb while sizes are unknown`() {
        assertNull(scrollbarThumb(Int.MAX_VALUE, 4000, 800, 800f, 48f))
        assertNull(scrollbarThumb(0, Int.MAX_VALUE, 800, 800f, 48f))
        assertNull(scrollbarThumb(0, 4000, Int.MAX_VALUE, 800f, 48f))
    }

    @Test
    fun `thumb length is the visible share of the track`() {
        assertEquals(ScrollbarThumb(0f, 200f), scrollbarThumb(0, 4000, 1000, 800f, 48f))
    }

    @Test
    fun `thumb is never shorter than the minimum`() {
        assertEquals(48f, scrollbarThumb(0, 1_000_000, 1000, 800f, 48f)!!.length)
    }

    @Test
    fun `thumb travels the free track as the list scrolls`() {
        assertEquals(300f, scrollbarThumb(1500, 4000, 1000, 800f, 48f)!!.start)
        assertEquals(600f, scrollbarThumb(3000, 4000, 1000, 800f, 48f)!!.start)
    }

    @Test
    fun `overscrolled offsets keep the thumb on the track`() {
        assertEquals(600f, scrollbarThumb(5000, 4000, 1000, 800f, 48f)!!.start)
    }

    @Test
    fun `dragging the thumb across the free track scrolls the whole range`() {
        assertEquals(3000f, scrollbarDragDistance(600f, 4000, 1000, 800f, 200f))
        assertEquals(-500f, scrollbarDragDistance(-100f, 4000, 1000, 800f, 200f))
    }

    @Test
    fun `a thumb filling the track does not scroll`() {
        assertEquals(0f, scrollbarDragDistance(10f, 4000, 1000, 800f, 800f))
    }
}
