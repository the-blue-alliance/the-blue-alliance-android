package com.thebluealliance.android.ui.common

import android.util.Log
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RefreshableViewModelTest {
    private class TestViewModel : RefreshableViewModel() {
        fun refresh(task: suspend () -> Unit) = refreshing(task)

        fun refreshReported(
            onFailed: suspend () -> Unit,
            vararg tasks: suspend () -> Unit,
        ) = refreshingReported(onFailed, *tasks)
    }

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        mockkStatic(Log::class)
        every { Log.w(any(), any<String>(), any()) } returns 0
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkStatic(Log::class)
    }

    @Test
    fun `stays refreshing until the last overlapping refresh finishes`() {
        val viewModel = TestViewModel()
        val first = CompletableDeferred<Unit>()
        val second = CompletableDeferred<Unit>()
        viewModel.refresh { first.await() }
        viewModel.refresh { second.await() }
        assertTrue(viewModel.isRefreshing.value)

        first.complete(Unit)
        assertTrue(viewModel.isRefreshing.value)

        second.complete(Unit)
        assertFalse(viewModel.isRefreshing.value)
    }

    @Test
    fun `reports once after every task finishes, if any failed to fetch`() {
        val viewModel = TestViewModel()
        val slow = CompletableDeferred<Unit>()
        var reports = 0
        viewModel.refreshReported(
            { reports++ },
            { throw java.io.IOException() },
            { throw java.io.IOException() },
            { slow.await() },
        )
        assertEquals(0, reports)
        slow.complete(Unit)
        assertEquals(1, reports)
    }

    @Test
    fun `doesn't report a failure that isn't a failed fetch`() {
        val viewModel = TestViewModel()
        var reports = 0
        viewModel.refreshReported({ reports++ }, { throw IllegalStateException() })
        assertEquals(0, reports)
    }

    @Test
    fun `doesn't report a cancelled refresh`() {
        val viewModel = TestViewModel()
        var reports = 0
        val job = viewModel.refreshReported({ reports++ }, { CompletableDeferred<Unit>().await() })
        job.cancel(CancellationException())
        assertEquals(0, reports)
    }
}
