package com.thebluealliance.android.ui.components

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class MatchFocusReturnTest {
    @Test
    fun `closing the pane returns focus to the match it showed`() {
        assertEquals("2025casf_qm5", matchFocusReturnKey("2025casf_qm5", null))
    }

    @Test
    fun `switching matches keeps focus where it is`() {
        assertNull(matchFocusReturnKey("2025casf_qm5", "2025casf_qm6"))
    }

    @Test
    fun `opening a match keeps focus where it is`() {
        assertNull(matchFocusReturnKey(null, "2025casf_qm5"))
    }
}
