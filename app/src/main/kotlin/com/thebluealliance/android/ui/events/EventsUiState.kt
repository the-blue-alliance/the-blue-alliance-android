package com.thebluealliance.android.ui.events

import com.thebluealliance.android.domain.model.Event

data class EventSection(val label: String, val events: List<Event>)

sealed interface EventsUiState {
    data object Loading : EventsUiState
    data class Success(
        val sections: List<EventSection>,
        val favoriteEventKeys: Set<String> = emptySet(),
    ) : EventsUiState
    data class Error(val message: String) : EventsUiState
}
