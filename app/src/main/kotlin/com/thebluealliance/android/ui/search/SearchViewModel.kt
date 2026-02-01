package com.thebluealliance.android.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thebluealliance.android.data.repository.EventRepository
import com.thebluealliance.android.data.repository.TeamRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val teamRepository: TeamRepository,
    private val eventRepository: EventRepository,
) : ViewModel() {

    private val _query = MutableStateFlow("")

    private val debouncedQuery = _query.debounce(300)

    private val teamsFlow = debouncedQuery.flatMapLatest { query ->
        if (query.length < 2) flowOf(emptyList())
        else teamRepository.searchTeams(query)
    }

    private val eventsFlow = debouncedQuery.flatMapLatest { query ->
        if (query.length < 2) flowOf(emptyList())
        else eventRepository.searchEvents(query)
    }

    val uiState: StateFlow<SearchUiState> = combine(
        _query,
        teamsFlow,
        eventsFlow,
    ) { query, teams, events ->
        SearchUiState(query = query, teams = teams, events = events)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SearchUiState())

    fun onQueryChanged(query: String) {
        _query.value = query
    }
}
