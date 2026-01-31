package com.thebluealliance.android.ui.events

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.thebluealliance.android.data.repository.EventRepository
import com.thebluealliance.android.data.repository.MatchRepository
import com.thebluealliance.android.data.repository.TeamRepository
import com.thebluealliance.android.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
@HiltViewModel
class EventDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val eventRepository: EventRepository,
    private val teamRepository: TeamRepository,
    private val matchRepository: MatchRepository,
) : ViewModel() {

    private val eventKey: String = savedStateHandle.toRoute<Screen.EventDetail>().eventKey

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val teamKeysFlow = teamRepository.observeEventTeamKeys(eventKey)

    private val teamsFlow = teamKeysFlow.flatMapLatest { keys ->
        if (keys.isEmpty()) flowOf(emptyList()) else teamRepository.observeTeams(keys)
    }

    val uiState: StateFlow<EventDetailUiState> = combine(
        eventRepository.observeEvent(eventKey),
        teamsFlow,
        matchRepository.observeEventMatches(eventKey),
        eventRepository.observeEventRankings(eventKey),
        combine(
            eventRepository.observeEventAlliances(eventKey),
            eventRepository.observeEventAwards(eventKey),
        ) { alliances, awards -> alliances to awards },
    ) { event, teams, matches, rankings, alliancesAndAwards ->
        val (alliances, awards) = alliancesAndAwards
        EventDetailUiState(
            event = event,
            teams = teams,
            matches = matches,
            rankings = rankings,
            alliances = alliances,
            awards = awards,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), EventDetailUiState())

    init {
        refreshAll()
    }

    fun refreshAll() {
        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                launch { teamRepository.refreshEventTeams(eventKey) }
                launch { matchRepository.refreshEventMatches(eventKey) }
                launch { eventRepository.refreshEventRankings(eventKey) }
                launch { eventRepository.refreshEventAlliances(eventKey) }
                launch { eventRepository.refreshEventAwards(eventKey) }
            } catch (_: Exception) {
                // Cached data from Room will still be shown
            } finally {
                _isRefreshing.value = false
            }
        }
    }
}
