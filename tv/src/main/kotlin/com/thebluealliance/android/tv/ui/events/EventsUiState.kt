package com.thebluealliance.android.tv.ui.events

import androidx.annotation.StringRes
import com.thebluealliance.android.tv.data.model.EventFeed

sealed interface EventsUiState {
    data object Loading : EventsUiState

    data class Success(
        val feed: EventFeed,
        val usingMockData: Boolean,
    ) : EventsUiState

    data class Error(
        @param:StringRes val messageRes: Int,
    ) : EventsUiState
}
