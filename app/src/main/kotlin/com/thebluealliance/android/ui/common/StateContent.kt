package com.thebluealliance.android.ui.common

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Wraps a screen body in pull-to-refresh and renders the right surface for [state],
 * reusing the shared [LoadingBox]/[EmptyBox]/[ErrorBox]. Callers supply only the
 * success [content].
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> StateContent(
    state: UiState<T>,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    empty: @Composable () -> Unit = { EmptyBox("Nothing here yet") },
    content: @Composable (T) -> Unit,
) {
    PullToRefreshBox(
        // Suppress the pull spinner during the initial load; LoadingBox already shows one.
        isRefreshing = isRefreshing && state !is UiState.Loading,
        onRefresh = onRefresh,
        modifier = modifier,
    ) {
        when (state) {
            UiState.Loading -> LoadingBox()
            UiState.Empty -> empty()
            is UiState.Error -> ErrorBox(message = state.message, onRetry = onRefresh)
            is UiState.Success -> content(state.data)
        }
    }
}
