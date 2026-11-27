package com.thebluealliance.android.ui.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.dp
import org.junit.jupiter.api.Assertions.assertFalse
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
                gutter = 8.dp,
                labelAtEnd = false,
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

    @Test
    fun `a disabled control keeps the arrow`() {
        assertFalse(Modifier.handCursor(enabled = false).showsHand())
    }

    @Test
    fun `the hand shows over a clickable item inside the column`() {
        assertTrue(
            isOverListItem(
                x = 500f,
                y = 150f,
                left = 100,
                right = 900,
                items =
                    listOf(
                        100 until 200,
                    ),
            ),
        )
    }

    @Test
    fun `the side margins keep the arrow`() {
        assertFalse(
            isOverListItem(
                x = 50f,
                y = 150f,
                left = 100,
                right = 900,
                items =
                    listOf(
                        100 until 200,
                    ),
            ),
        )
        assertFalse(
            isOverListItem(
                x = 950f,
                y = 150f,
                left = 100,
                right = 900,
                items =
                    listOf(
                        100 until 200,
                    ),
            ),
        )
    }

    @Test
    fun `blank space past the last item keeps the arrow`() {
        assertFalse(
            isOverListItem(
                x = 500f,
                y = 250f,
                left = 100,
                right = 900,
                items =
                    listOf(
                        100 until 200,
                    ),
            ),
        )
    }
}
