package com.thebluealliance.android.ui.mytba

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.content.Context
import com.thebluealliance.android.data.repository.AuthRepository
import com.thebluealliance.android.data.repository.EventRepository
import com.thebluealliance.android.data.repository.MatchRepository
import com.thebluealliance.android.data.repository.MyTBARepository
import com.thebluealliance.android.domain.model.Favorite
import com.thebluealliance.android.domain.model.ModelType
import com.thebluealliance.android.messaging.DeviceRegistrationManager
import com.thebluealliance.android.shortcuts.TBAShortcutManager
import com.thebluealliance.android.tracking.MatchTrackingService
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.LocalDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyTBAViewModel @Inject constructor(
    @ApplicationContext private val appContext: Context,
    private val authRepository: AuthRepository,
    private val myTBARepository: MyTBARepository,
    private val eventRepository: EventRepository,
    private val matchRepository: MatchRepository,
    private val deviceRegistrationManager: DeviceRegistrationManager,
    private val shortcutManager: TBAShortcutManager,
) : ViewModel() {

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _trackedTeamKey = MutableStateFlow(MatchTrackingService.activeTeamKey)

    private val _trackingMessage = MutableStateFlow<String?>(null)
    val trackingMessage: StateFlow<String?> = _trackingMessage.asStateFlow()

    val uiState: StateFlow<MyTBAUiState> = combine(
        authRepository.currentUser,
        myTBARepository.observeFavorites(),
        myTBARepository.observeSubscriptions(),
        _trackedTeamKey,
    ) { user, favorites, subscriptions, trackedTeamKey ->
        MyTBAUiState(
            isSignedIn = user != null,
            userName = user?.displayName,
            userEmail = user?.email,
            userPhotoUrl = user?.photoUrl?.toString(),
            favorites = favorites,
            subscriptions = subscriptions,
            canPinShortcuts = shortcutManager.canPinShortcuts(),
            trackedTeamKey = trackedTeamKey,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), MyTBAUiState())

    init {
        // Auto-refresh when user signs in
        viewModelScope.launch {
            authRepository.currentUser
                .map { it != null }
                .distinctUntilChanged()
                .collect { signedIn ->
                    if (signedIn) {
                        launch { try { deviceRegistrationManager.onSignIn() } catch (_: Exception) {} }
                        refresh()
                    }
                }
        }
    }

    fun startTracking(teamKey: String) {
        viewModelScope.launch {
            try {
                val year = LocalDate.now().year
                // Refresh team events to ensure we have current data
                eventRepository.refreshTeamEvents(teamKey, year)

                // Find events happening around today
                val today = LocalDate.now()
                val events = eventRepository.observeTeamEvents(teamKey, year).first()
                val currentEvents = events.filter { event ->
                    val start = event.startDate?.let { LocalDate.parse(it) }
                    val end = event.endDate?.let { LocalDate.parse(it) }
                    if (start != null && end != null) {
                        !today.isBefore(start) && !today.isAfter(end.plusDays(1))
                    } else false
                }

                when {
                    currentEvents.isEmpty() -> {
                        val teamNumber = teamKey.removePrefix("frc")
                        _trackingMessage.value = "Team $teamNumber isn't competing right now"
                    }
                    currentEvents.size == 1 -> {
                        val event = currentEvents.first()
                        // Refresh match data for this event
                        matchRepository.refreshEventMatches(event.key)
                        MatchTrackingService.start(appContext, teamKey, event.key)
                        _trackedTeamKey.value = teamKey
                    }
                    else -> {
                        // Multiple events — for now, pick the first one
                        // TODO: Show picker dialog for multiple events
                        val event = currentEvents.first()
                        matchRepository.refreshEventMatches(event.key)
                        MatchTrackingService.start(appContext, teamKey, event.key)
                        _trackedTeamKey.value = teamKey
                    }
                }
            } catch (e: Exception) {
                _trackingMessage.value = "Couldn't start tracking: ${e.message}"
            }
        }
    }

    fun stopTracking() {
        MatchTrackingService.stop(appContext)
        _trackedTeamKey.value = null
    }

    fun clearTrackingMessage() {
        _trackingMessage.value = null
    }

    fun requestPinShortcut(favorite: Favorite) {
        viewModelScope.launch {
            shortcutManager.requestPinShortcut(favorite)
        }
    }

    fun refresh() {
        viewModelScope.launch {
            if (!authRepository.isSignedIn()) return@launch
            _isRefreshing.value = true
            try {
                coroutineScope {
                    launch { try { myTBARepository.refreshFavorites() } catch (_: Exception) {} }
                    launch { try { myTBARepository.refreshSubscriptions() } catch (_: Exception) {} }
                }
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            try { deviceRegistrationManager.onSignOut() } catch (_: Exception) {}
            authRepository.signOut()
            myTBARepository.clearLocal()
        }
    }
}
