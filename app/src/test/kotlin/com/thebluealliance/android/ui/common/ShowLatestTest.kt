package com.thebluealliance.android.ui.common

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ShowLatestTest {
    @Test
    fun `a new message replaces the one showing`() =
        runTest {
            val messages = Channel<String>(Channel.BUFFERED)
            val events = mutableListOf<String>()
            val job =
                launch {
                    messages.receiveAsFlow().showLatest { message ->
                        events += "show $message"
                        try {
                            awaitCancellation()
                        } catch (e: CancellationException) {
                            events += "dismiss $message"
                            throw e
                        }
                    }
                }
            messages.send("first")
            runCurrent()
            messages.send("second")
            runCurrent()
            assertEquals(listOf("show first", "dismiss first", "show second"), events)
            job.cancel()
        }
}
