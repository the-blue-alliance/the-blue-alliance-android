package com.thebluealliance.android.ui.common

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RefreshableViewModelTest {
    private class TestViewModel : RefreshableViewModel() {
        fun refresh(task: suspend () -> Unit) = refreshing(task)
    }

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
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
}
