package com.thebluealliance.android.ui.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class HandCursorTest {
    private val hand = Modifier.pointerHoverIcon(PointerIcon.Hand)

    private fun Modifier.showsHand() = any { it == hand }

    @Test
    fun `handCursor sets the hand pointer`() {
        assertTrue(Modifier.handCursor().showsHand())
    }

    @Test
    fun `paddedClickable shows the hand`() {
        assertTrue(Modifier.paddedClickable(RoundedCornerShape(8)) {}.showsHand())
    }

    @Test
    fun `sortableHeader shows the hand`() {
        val header =
            Modifier.sortableHeader(
                shape = RoundedCornerShape(8),
                label = "Rank",
                sorted = false,
                ascending = true,
            ) {}
        assertTrue(header.showsHand())
    }

    @Test
    fun `expandableRow shows the hand`() {
        assertTrue(Modifier.expandableRow(expanded = false) {}.showsHand())
    }
}
