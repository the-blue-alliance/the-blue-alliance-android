package com.thebluealliance.android.shortcuts

import app.cash.turbine.test
import com.thebluealliance.android.domain.model.Favorite
import com.thebluealliance.android.domain.model.ModelType
import com.thebluealliance.android.ui.common.UserMessageBus
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class TBAShortcutManagerMessageTest {
    @Test
    fun `a favorite with no shortcut says it couldn't create one`() =
        runTest {
            val bus = UserMessageBus()
            val manager =
                TBAShortcutManager(
                    context = mockk(relaxed = true),
                    tbaRepository = mockk(relaxed = true),
                    eventRepository = mockk(relaxed = true),
                    teamRepository = mockk(relaxed = true),
                    seasons = mockk { coEvery { defaultSeason() } returns 2026 },
                    userMessages = bus,
                )
            manager.requestPinShortcut(Favorite("2024casj_qm1", ModelType.MATCH))
            bus.messages.test { assertEquals("Couldn't create shortcut", awaitItem().text) }
        }
}
