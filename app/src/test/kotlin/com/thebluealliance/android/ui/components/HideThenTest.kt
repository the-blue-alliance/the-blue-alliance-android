package com.thebluealliance.android.ui.components

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HideThenTest {
    @Test
    fun `runs the action only after the hide finishes`() =
        runTest {
            val hidden = CompletableDeferred<Unit>()
            val events = mutableListOf<String>()
            hideThen({ hidden.await() }) { events += "then" }
            runCurrent()
            assertEquals(emptyList<String>(), events)
            hidden.complete(Unit)
            runCurrent()
            assertEquals(listOf("then"), events)
        }

    @Test
    fun `runs the action when the hide is interrupted`() =
        runTest {
            val events = mutableListOf<String>()
            val job = hideThen({ CompletableDeferred<Unit>().await() }) { events += "then" }
            runCurrent()
            job.cancel()
            runCurrent()
            assertEquals(listOf("then"), events)
        }
}
