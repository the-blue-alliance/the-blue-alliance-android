package com.thebluealliance.android.ui.search

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thebluealliance.android.data.repository.EventRepository
import com.thebluealliance.android.data.repository.TeamRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class, kotlinx.coroutines.FlowPreview::class)
@HiltViewModel
class SearchViewModel
    @Inject
    constructor(
        private val teamRepository: TeamRepository,
        private val eventRepository: EventRepository,
    ) : ViewModel() {
        /** Owned here so the query survives configuration changes alongside the results. */
        val queryState = TextFieldState()

        private val query =
            snapshotFlow { queryState.text.toString() }
                .stateIn(viewModelScope, SharingStarted.Eagerly, "")

        private val debouncedQuery = query.debounce(300)

        private val teamsFlow =
            debouncedQuery.flatMapLatest { query ->
                if (query.length == 0) {
                    flowOf(emptyList())
                } else {
                    teamRepository.searchTeams(query)
                }
            }

        private val eventsFlow =
            debouncedQuery.flatMapLatest { query ->
                if (query.length == 0) {
                    flowOf(emptyList())
                } else {
                    eventRepository.searchEvents(query)
                }
            }

        val uiState: StateFlow<SearchUiState> =
            combine(
                query,
                debouncedQuery,
                teamsFlow,
                eventsFlow,
            ) { query, searchedQuery, teams, events ->
                SearchUiState(
                    query = query,
                    searchedQuery = searchedQuery,
                    teams = teams,
                    events = events,
                )
            }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SearchUiState())
    }
