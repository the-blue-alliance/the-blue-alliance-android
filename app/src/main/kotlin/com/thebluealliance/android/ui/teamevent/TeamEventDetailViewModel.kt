package com.thebluealliance.android.ui.teamevent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thebluealliance.android.data.repository.EventRepository
import com.thebluealliance.android.data.repository.MatchRepository
import com.thebluealliance.android.data.repository.TeamRepository
import com.thebluealliance.android.navigation.Screen
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = TeamEventDetailViewModel.Factory::class)
class TeamEventDetailViewModel @AssistedInject constructor(
    @Assisted val navKey: Screen.TeamEventDetail,
    private val teamRepository: TeamRepository,
    private val eventRepository: EventRepository,
    private val matchRepository: MatchRepository,
) : ViewModel() {

    val teamKey: String = navKey.teamKey
    val eventKey: String = navKey.eventKey

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    val uiState: StateFlow<TeamEventDetailUiState> = combine(
        teamRepository.observeTeam(teamKey),
        eventRepository.observeEvent(eventKey),
        eventRepository.observeEventRankings(eventKey).map { rankings ->
            rankings.firstOrNull { it.teamKey == teamKey }
        },
        matchRepository.observeEventMatches(eventKey).map { matches ->
            matches.filter { m -> teamKey in m.redTeamKeys || teamKey in m.blueTeamKeys }
        },
        eventRepository.observeEventAwards(eventKey).map { awards ->
            awards.filter { it.teamKey == teamKey }
        },
    ) { team, event, ranking, matches, awards ->
        TeamEventDetailUiState(
            team = team,
            event = event,
            ranking = ranking,
            matches = matches,
            awards = awards,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TeamEventDetailUiState())

    init {
        refreshAll()
    }

    fun refreshAll() {
        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                coroutineScope {
                    launch { try { teamRepository.refreshTeam(teamKey) } catch (_: Exception) {} }
                    launch { try { eventRepository.refreshEvent(eventKey) } catch (_: Exception) {} }
                    launch { try { matchRepository.refreshEventMatches(eventKey) } catch (_: Exception) {} }
                    launch { try { eventRepository.refreshEventRankings(eventKey) } catch (_: Exception) {} }
                    launch { try { eventRepository.refreshEventAwards(eventKey) } catch (_: Exception) {} }
                }
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(navKey: Screen.TeamEventDetail): TeamEventDetailViewModel
    }
}
