package com.thebluealliance.android.ui.components

import androidx.compose.ui.unit.dp
import com.thebluealliance.android.ui.theme.FocusRingDepth
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class SwitchRowPaddingTest {
    // Material 3's switch track is 32dp tall.
    private val switchHeight = 32.dp

    @Test
    fun `switch row stays a 48dp target`() {
        assertTrue(switchHeight + SwitchRowVerticalPadding * 2 >= 48.dp)
    }

    @Test
    fun `focus ring clears the switch`() {
        assertTrue(SwitchRowVerticalPadding > FocusRingDepth)
    }
}
