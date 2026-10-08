package com.thebluealliance.android.tv.ui.events

import androidx.annotation.StringRes
import com.thebluealliance.android.tv.data.model.EventFeed
import java.time.LocalDate

sealed interface EventsUiState {
    data object Loading : EventsUiState

    data class Success(
        val feed: EventFeed,
        val usingMockData: Boolean,
        /** The day [feed] was built for; cards date their status badges against the same day. */
        val today: LocalDate,
    ) : EventsUiState

    data class Error(
        @param:StringRes val messageRes: Int,
    ) : EventsUiState
}
