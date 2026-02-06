package com.thebluealliance.android.ui.teams

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.thebluealliance.android.data.remote.TbaApi
import com.thebluealliance.android.data.repository.AuthRepository
import com.thebluealliance.android.data.repository.EventRepository
import com.thebluealliance.android.data.repository.MyTBARepository
import com.thebluealliance.android.data.repository.TeamRepository
import com.thebluealliance.android.domain.model.ModelType
import com.thebluealliance.android.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class TeamDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val teamRepository: TeamRepository,
    private val eventRepository: EventRepository,
    private val myTBARepository: MyTBARepository,
    private val authRepository: AuthRepository,
    private val tbaApi: TbaApi,
) : ViewModel() {

    private val teamKey: String = savedStateHandle.toRoute<Screen.TeamDetail>().teamKey

    private val _selectedYear = MutableStateFlow(Calendar.getInstance().get(Calendar.YEAR))
    val selectedYear: StateFlow<Int> = _selectedYear.asStateFlow()

    private val _yearsParticipated = MutableStateFlow<List<Int>>(emptyList())
    val yearsParticipated: StateFlow<List<Int>> = _yearsParticipated.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    val uiState: StateFlow<TeamDetailUiState> = combine(
        teamRepository.observeTeam(teamKey),
        _selectedYear.flatMapLatest { year ->
            eventRepository.observeTeamEvents(teamKey, year)
        },
        _selectedYear.flatMapLatest { year ->
            teamRepository.observeTeamMedia(teamKey, year)
        },
    ) { team, events, media ->
        TeamDetailUiState(
            team = team,
            events = events,
            media = media,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TeamDetailUiState())

    val isFavorite: StateFlow<Boolean> = myTBARepository.isFavorite(teamKey, ModelType.TEAM)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    private val _showSignInPrompt = MutableSharedFlow<Unit>()
    val showSignInPrompt: SharedFlow<Unit> = _showSignInPrompt.asSharedFlow()

    init {
        fetchYearsParticipated()
        refreshAll()
    }

    private fun fetchYearsParticipated() {
        viewModelScope.launch {
            try {
                val years = tbaApi.getTeamYearsParticipated(teamKey).sortedDescending()
                _yearsParticipated.value = years
                if (years.isNotEmpty()) {
                    _selectedYear.value = years.first()
                    refreshYearData()
                }
            } catch (_: Exception) {
                // Fallback: generate year range from rookieYear if available
                buildYearsFromRookieYear()
            }
        }
    }

    private fun buildYearsFromRookieYear() {
        viewModelScope.launch {
            teamRepository.observeTeam(teamKey).collect { team ->
                if (team != null && _yearsParticipated.value.isEmpty()) {
                    val rookieYear = team.rookieYear ?: return@collect
                    val maxYear = Calendar.getInstance().get(Calendar.YEAR)
                    val years = (maxYear downTo rookieYear).toList()
                    _yearsParticipated.value = years
                    _selectedYear.value = years.first()
                    refreshYearData()
                }
            }
        }
    }

    fun selectYear(year: Int) {
        _selectedYear.value = year
        refreshYearData()
    }

    fun toggleFavorite() {
        viewModelScope.launch {
            if (!authRepository.isSignedIn()) {
                _showSignInPrompt.emit(Unit)
                return@launch
            }
            try {
                if (isFavorite.value) {
                    myTBARepository.removeFavorite(teamKey, ModelType.TEAM)
                } else {
                    myTBARepository.addFavorite(teamKey, ModelType.TEAM)
                }
            } catch (_: Exception) {}
        }
    }

    fun refreshAll() {
        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                launch { try { teamRepository.refreshTeam(teamKey) } catch (_: Exception) {} }
                refreshYearData()
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    private fun refreshYearData() {
        val year = _selectedYear.value
        viewModelScope.launch {
            launch { try { eventRepository.refreshTeamEvents(teamKey, year) } catch (_: Exception) {} }
            launch { try { teamRepository.refreshTeamMedia(teamKey, year) } catch (_: Exception) {} }
        }
    }
}
