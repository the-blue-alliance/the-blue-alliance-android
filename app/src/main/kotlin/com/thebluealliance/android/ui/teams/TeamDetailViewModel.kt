package com.thebluealliance.android.ui.teams

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.thebluealliance.android.data.repository.EventRepository
import com.thebluealliance.android.data.repository.TeamRepository
import com.thebluealliance.android.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class TeamDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val teamRepository: TeamRepository,
    private val eventRepository: EventRepository,
) : ViewModel() {

    private val teamKey: String = savedStateHandle.toRoute<Screen.TeamDetail>().teamKey
    private val currentYear: Int = Calendar.getInstance().get(Calendar.YEAR)

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    val uiState: StateFlow<TeamDetailUiState> = combine(
        teamRepository.observeTeam(teamKey),
        eventRepository.observeTeamEvents(teamKey, currentYear),
        teamRepository.observeTeamMedia(teamKey, currentYear),
    ) { team, events, media ->
        TeamDetailUiState(
            team = team,
            events = events,
            media = media,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TeamDetailUiState())

    init {
        refreshAll()
    }

    fun refreshAll() {
        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                launch { try { teamRepository.refreshTeam(teamKey) } catch (_: Exception) {} }
                launch { try { eventRepository.refreshTeamEvents(teamKey, currentYear) } catch (_: Exception) {} }
                launch { try { teamRepository.refreshTeamMedia(teamKey, currentYear) } catch (_: Exception) {} }
            } finally {
                _isRefreshing.value = false
            }
        }
    }
}
