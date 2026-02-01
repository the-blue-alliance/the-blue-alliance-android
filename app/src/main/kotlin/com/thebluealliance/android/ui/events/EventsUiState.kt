package com.thebluealliance.android.ui.events

import com.thebluealliance.android.domain.model.Event

sealed interface EventsUiState {
    data object Loading : EventsUiState
    data class Success(
        val eventsByWeek: Map<Int?, List<Event>>,
        val favoriteEventKeys: Set<String> = emptySet(),
    ) : EventsUiState
    data class Error(val message: String) : EventsUiState
}
