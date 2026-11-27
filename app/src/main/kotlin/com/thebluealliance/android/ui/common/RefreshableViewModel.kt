package com.thebluealliance.android.ui.common

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import java.util.concurrent.ConcurrentHashMap

enum class RefreshOutcome { PENDING, SUCCEEDED, FAILED }

/**
 * Whether a refresh failed to get the data: no connection, a timeout, or any HTTP error but a 404
 * (a rejected key, rate limiting or a server error). A 404 or an empty body means the server has
 * no such data, which is not a failure.
 */
fun Throwable.isFetchFailure(): Boolean =
    this is IOException || (this is HttpException && code() != 404)

/** Whether the server answered that the requested thing doesn't exist. */
fun Throwable.isNotFound(): Boolean = this is HttpException && code() == 404

/**
 * Base for screens that pull-to-refresh. Owns the shared [isRefreshing] flag and runs
 * refresh tasks concurrently, logging (instead of swallowing) any failure. Overlapping refreshes
 * keep [isRefreshing] set until the last one finishes.
 */
abstract class RefreshableViewModel : ViewModel() {
    private val _isRefreshing = MutableStateFlow(false)
    private val inFlight =
        java.util.concurrent.atomic
            .AtomicInteger(0)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    /**
     * Like [refreshing], but records completion into [outcome] — an empty cache only
     * means Empty/Error once a refresh has actually finished.
     */
    protected fun refreshingTracked(
        outcome: MutableStateFlow<RefreshOutcome>,
        vararg tasks: suspend () -> Unit,
    ) {
        val remaining =
            java.util.concurrent.atomic
                .AtomicInteger(tasks.size)
        val anyFailed =
            java.util.concurrent.atomic
                .AtomicBoolean(false)
        val tracked =
            tasks.map { task ->
                suspend {
                    try {
                        task()
                    } catch (e: CancellationException) {
                        throw e
                    } catch (e: Exception) {
                        anyFailed.set(true)
                        throw e
                    } finally {
                        if (remaining.decrementAndGet() == 0) {
                            outcome.value =
                                if (anyFailed.get()) {
                                    RefreshOutcome.FAILED
                                } else {
                                    RefreshOutcome.SUCCEEDED
                                }
                        }
                    }
                }
            }
        refreshing(*tracked.toTypedArray())
    }

    /**
     * Runs [tasks] like [refreshing], recording each tab's outcome into [outcomes] so an empty
     * tab can tell "failed" from "none", then passes the tabs that failed to [onDone]. A null tab
     * marks a supporting fetch that no tab's state depends on.
     */
    protected fun <T : Any> refreshTabs(
        outcomes: MutableStateFlow<Map<T, RefreshOutcome>>,
        onDone: suspend (failed: Set<T>) -> Unit,
        vararg tasks: Pair<T?, suspend () -> Unit>,
    ) {
        val failed = ConcurrentHashMap.newKeySet<T>()
        val remaining =
            java.util.concurrent.atomic
                .AtomicInteger(tasks.size)

        fun markSucceeded(tab: T) =
            outcomes.update { if (tab in failed) it else it + (tab to RefreshOutcome.SUCCEEDED) }
        val tracked: List<suspend () -> Unit> =
            tasks.map { (tab, task) ->
                {
                    try {
                        task()
                        if (tab != null) markSucceeded(tab)
                    } catch (e: CancellationException) {
                        throw e
                    } catch (e: Exception) {
                        if (tab != null) {
                            if (e.isFetchFailure()) {
                                failed += tab
                                outcomes.update { it + (tab to RefreshOutcome.FAILED) }
                            } else {
                                // The server answered with nothing: empty, not failed.
                                markSucceeded(tab)
                            }
                        }
                        throw e
                    } finally {
                        if (remaining.decrementAndGet() == 0) onDone(failed)
                    }
                }
            }
        refreshing(*tracked.toTypedArray())
    }

    protected fun refreshing(vararg tasks: suspend () -> Unit): Job =
        viewModelScope.launch {
            inFlight.incrementAndGet()
            _isRefreshing.value = true
            try {
                coroutineScope {
                    tasks.forEach { task ->
                        launch {
                            try {
                                task()
                            } catch (e: CancellationException) {
                                throw e
                            } catch (e: Exception) {
                                Log.w(TAG, "refresh failed", e)
                            }
                        }
                    }
                }
            } finally {
                if (inFlight.decrementAndGet() == 0) _isRefreshing.value = false
            }
        }

    private companion object {
        const val TAG = "RefreshableViewModel"
    }
}
