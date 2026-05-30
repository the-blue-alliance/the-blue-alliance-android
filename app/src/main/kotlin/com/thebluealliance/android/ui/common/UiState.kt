package com.thebluealliance.android.ui.common

/** Canonical screen state: loading, loaded-with-data, loaded-but-empty, or failed. */
sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>

    data class Success<T>(
        val data: T,
    ) : UiState<T>

    data object Empty : UiState<Nothing>

    data class Error(
        val message: String? = null,
    ) : UiState<Nothing>
}
