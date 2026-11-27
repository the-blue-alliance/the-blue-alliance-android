package com.thebluealliance.android.ui.components

import androidx.compose.material3.Typography
import androidx.compose.ui.unit.dp
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class LinkTargetSizeTest {
    private val typography = Typography()

    @Test
    fun `bodySmall text link reaches the minimum link height`() {
        val line = typography.bodySmall.lineHeight.value.dp
        assertTrue(line + SmallTextLinkVerticalPadding * 2 >= MinLinkHeight)
    }

    @Test
    fun `minimum link height is the 48dp target`() {
        assertEquals(48.dp, MinLinkHeight)
    }

    @Test
    fun `inset row keeps its label on the 16dp keyline`() {
        assertEquals(16.dp, LinkRowInset * 2)
    }
}
