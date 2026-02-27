package com.thebluealliance.android.ui.teams

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thebluealliance.android.data.remote.TbaApi
import com.thebluealliance.android.data.repository.AuthRepository
import com.thebluealliance.android.data.repository.EventRepository
import com.thebluealliance.android.data.repository.MyTBARepository
import com.thebluealliance.android.data.repository.TeamRepository
import com.thebluealliance.android.domain.model.ModelType
import com.thebluealliance.android.domain.model.Subscription
import com.thebluealliance.android.navigation.Screen
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.coroutineScope
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

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel(assistedFactory = TeamDetailViewModel.Factory::class)
class TeamDetailViewModel @AssistedInject constructor(
    @Assisted val navKey: Screen.TeamDetail,
    private val teamRepository: TeamRepository,
    private val eventRepository: EventRepository,
    private val myTBARepository: MyTBARepository,
    private val authRepository: AuthRepository,
    private val tbaApi: TbaApi,
) : ViewModel() {

    private val teamKey: String = navKey.teamKey.let { key ->
        // Deep links from thebluealliance.com use /team/177 (number only),
        // but the API/DB key format is "frc177".
        if (key.all { it.isDigit() }) "frc$key" else key
    }

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

    val subscription: StateFlow<Subscription?> = myTBARepository
        .observeSubscription(teamKey, ModelType.TEAM)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _showSignInPrompt = MutableSharedFlow<Unit>()
    val showSignInPrompt: SharedFlow<Unit> = _showSignInPrompt.asSharedFlow()

    private val _userMessage = MutableSharedFlow<String>()
    val userMessage: SharedFlow<String> = _userMessage.asSharedFlow()

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
        viewModelScope.launch { refreshYearData() }
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

    fun updatePreferences(favorite: Boolean, notifications: List<String>) {
        viewModelScope.launch {
            try {
                myTBARepository.updatePreferences(teamKey, ModelType.TEAM, favorite, notifications)
            } catch (_: Exception) {
                _userMessage.emit("Failed to save notification preferences")
            }
        }
    }

    fun isSignedIn(): Boolean = authRepository.isSignedIn()

    fun refreshAll() {
        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                coroutineScope {
                    launch { try { teamRepository.refreshTeam(teamKey) } catch (_: Exception) {} }
                    launch { refreshYearData() }
                }
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    private suspend fun refreshYearData() {
        val year = _selectedYear.value
        coroutineScope {
            launch { try { eventRepository.refreshTeamEvents(teamKey, year) } catch (_: Exception) {} }
            launch { try { teamRepository.refreshTeamMedia(teamKey, year) } catch (_: Exception) {} }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(navKey: Screen.TeamDetail): TeamDetailViewModel
    }
}
