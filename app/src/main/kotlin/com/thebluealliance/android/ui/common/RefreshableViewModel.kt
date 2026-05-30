package com.thebluealliance.android.ui.common

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Base for screens that pull-to-refresh. Owns the shared [isRefreshing] flag and runs
 * refresh tasks concurrently, logging (instead of swallowing) any failure.
 */
abstract class RefreshableViewModel : ViewModel() {
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    protected fun refreshing(vararg tasks: suspend () -> Unit) {
        viewModelScope.launch {
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
                _isRefreshing.value = false
            }
        }
    }

    private companion object {
        const val TAG = "RefreshableViewModel"
    }
}
