package com.thebluealliance.android.wear.tracker

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class TeamNumberTest {
    @Test
    fun `keeps a plain team number`() {
        assertEquals("254", TeamNumber.normalize("254"))
    }

    @Test
    fun `strips leading zeros`() {
        assertEquals("18", TeamNumber.normalize("018"))
        assertEquals("254", TeamNumber.normalize("0254"))
    }

    @Test
    fun `drops non-digits`() {
        assertEquals("254", TeamNumber.normalize(" 2 5 4 "))
    }

    @Test
    fun `rejects empty input`() {
        assertNull(TeamNumber.normalize(""))
    }

    @Test
    fun `rejects zero`() {
        assertNull(TeamNumber.normalize("0"))
        assertNull(TeamNumber.normalize("000"))
    }

    @Test
    fun `rejects numbers longer than five digits`() {
        assertNull(TeamNumber.normalize("123456"))
    }
}
