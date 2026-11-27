package com.thebluealliance.android.ui.search

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class SearchFieldEscapeTest {
    @Test
    fun `Esc clears a typed query`() {
        assertEquals(SearchFieldEscape.ClearText, searchFieldEscape("cheesy"))
    }

    @Test
    fun `Esc in an empty field leaves the field`() {
        assertEquals(SearchFieldEscape.ClearFocus, searchFieldEscape(""))
    }
}
