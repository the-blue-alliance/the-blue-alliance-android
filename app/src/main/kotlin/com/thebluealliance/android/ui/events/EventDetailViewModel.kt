package com.thebluealliance.android.ui.events

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.thebluealliance.android.data.repository.AuthRepository
import com.thebluealliance.android.data.repository.EventRepository
import com.thebluealliance.android.data.repository.MatchRepository
import com.thebluealliance.android.data.repository.MyTBARepository
import com.thebluealliance.android.data.repository.TeamRepository
import com.thebluealliance.android.domain.model.ModelType
import com.thebluealliance.android.domain.model.Subscription
import com.thebluealliance.android.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
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
    private val myTBARepository: MyTBARepository,
    private val authRepository: AuthRepository,
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

    val isFavorite: StateFlow<Boolean> = myTBARepository.isFavorite(eventKey, ModelType.EVENT)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val subscription: StateFlow<Subscription?> = myTBARepository
        .observeSubscription(eventKey, ModelType.EVENT)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _showSignInPrompt = MutableSharedFlow<Unit>()
    val showSignInPrompt: SharedFlow<Unit> = _showSignInPrompt.asSharedFlow()

    private val _userMessage = MutableSharedFlow<String>()
    val userMessage: SharedFlow<String> = _userMessage.asSharedFlow()

    init {
        refreshAll()
    }

    fun toggleFavorite() {
        viewModelScope.launch {
            if (!authRepository.isSignedIn()) {
                _showSignInPrompt.emit(Unit)
                return@launch
            }
            try {
                if (isFavorite.value) {
                    myTBARepository.removeFavorite(eventKey, ModelType.EVENT)
                } else {
                    myTBARepository.addFavorite(eventKey, ModelType.EVENT)
                }
            } catch (_: Exception) {}
        }
    }

    fun updatePreferences(favorite: Boolean, notifications: List<String>) {
        viewModelScope.launch {
            try {
                myTBARepository.updatePreferences(eventKey, ModelType.EVENT, favorite, notifications)
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
                launch { try { eventRepository.refreshEvent(eventKey) } catch (_: Exception) {} }
                launch { try { teamRepository.refreshEventTeams(eventKey) } catch (_: Exception) {} }
                launch { try { matchRepository.refreshEventMatches(eventKey) } catch (_: Exception) {} }
                launch { try { eventRepository.refreshEventRankings(eventKey) } catch (_: Exception) {} }
                launch { try { eventRepository.refreshEventAlliances(eventKey) } catch (_: Exception) {} }
                launch { try { eventRepository.refreshEventAwards(eventKey) } catch (_: Exception) {} }
            } finally {
                _isRefreshing.value = false
            }
        }
    }
}
