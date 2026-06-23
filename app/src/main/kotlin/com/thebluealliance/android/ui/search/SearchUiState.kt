package com.thebluealliance.android.ui.search

import com.thebluealliance.android.domain.model.Event
import com.thebluealliance.android.domain.model.Team

data class SearchUiState(
    val query: String = "",
    /** The debounced query the current [teams]/[events] correspond to. */
    val searchedQuery: String = "",
    val teams: List<Team> = emptyList(),
    val events: List<Event> = emptyList(),
)
