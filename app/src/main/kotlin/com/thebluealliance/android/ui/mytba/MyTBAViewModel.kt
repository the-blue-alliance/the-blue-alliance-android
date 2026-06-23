package com.thebluealliance.android.ui.mytba

import androidx.lifecycle.viewModelScope
import com.thebluealliance.android.data.repository.AuthRepository
import com.thebluealliance.android.data.repository.EventRepository
import com.thebluealliance.android.data.repository.MatchRepository
import com.thebluealliance.android.data.repository.MyTBARepository
import com.thebluealliance.android.data.repository.TeamRepository
import com.thebluealliance.android.domain.getFullLabel
import com.thebluealliance.android.domain.model.Event
import com.thebluealliance.android.domain.model.Favorite
import com.thebluealliance.android.domain.model.Match
import com.thebluealliance.android.domain.model.ModelType
import com.thebluealliance.android.domain.model.Team
import com.thebluealliance.android.messaging.DeviceRegistrationManager
import com.thebluealliance.android.shortcuts.TBAShortcutManager
import com.thebluealliance.android.ui.common.RefreshableViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class MyTBAViewModel
    @Inject
    constructor(
        private val authRepository: AuthRepository,
        private val myTBARepository: MyTBARepository,
        private val deviceRegistrationManager: DeviceRegistrationManager,
        private val shortcutManager: TBAShortcutManager,
        teamRepository: TeamRepository,
        eventRepository: EventRepository,
        matchRepository: MatchRepository,
    ) : RefreshableViewModel() {
        private val displayNames: Flow<Map<String, String>> =
            combine(
                myTBARepository.observeFavorites(),
                myTBARepository.observeSubscriptions(),
            ) { favorites, subscriptions ->
                (
                    favorites.map { it.modelKey to it.modelType } +
                        subscriptions.map { it.modelKey to it.modelType }
                ).distinct()
            }.distinctUntilChanged().flatMapLatest { keyedTypes ->
                fun keysOf(type: Int) = keyedTypes.filter { it.second == type }.map { it.first }
                val matchKeys = keysOf(ModelType.MATCH)
                val eventKeys =
                    (keysOf(ModelType.EVENT) + matchKeys.map { it.substringBefore('_') }).distinct()
                combine(
                    teamRepository.observeTeams(keysOf(ModelType.TEAM)),
                    eventRepository.observeEvents(eventKeys),
                    matchRepository.observeMatches(matchKeys),
                ) { teams, events, matches -> buildMyTBADisplayNames(teams, events, matches) }
            }

        val uiState: StateFlow<MyTBAUiState> =
            combine(
                authRepository.currentUser,
                myTBARepository.observeFavorites(),
                myTBARepository.observeSubscriptions(),
                displayNames,
            ) { user, favorites, subscriptions, displayNames ->
                MyTBAUiState(
                    isSignedIn = user != null,
                    userName = user?.displayName,
                    userEmail = user?.email,
                    userPhotoUrl = user?.photoUrl?.toString(),
                    favorites = favorites,
                    subscriptions = subscriptions,
                    displayNames = displayNames,
                    canPinShortcuts = shortcutManager.canPinShortcuts(),
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
                            launch {
                                try {
                                    deviceRegistrationManager.onSignIn()
                                } catch (
                                    _: Exception,
                                ) {
                                }
                            }
                            refresh()
                        }
                    }
            }
        }

        fun requestPinShortcut(favorite: Favorite) {
            viewModelScope.launch {
                shortcutManager.requestPinShortcut(favorite)
            }
        }

        fun removeFavorite(favorite: Favorite) {
            viewModelScope.launch {
                try {
                    myTBARepository.removeFavorite(favorite.modelKey, favorite.modelType)
                } catch (e: Exception) {
                    android.util.Log.w("MyTBAViewModel", "Failed to remove favorite", e)
                }
            }
        }

        fun removeSubscription(
            modelKey: String,
            modelType: Int,
        ) {
            viewModelScope.launch {
                try {
                    val isFavorited =
                        uiState.value.favorites.any {
                            it.modelKey == modelKey && it.modelType == modelType
                        }
                    myTBARepository.updatePreferences(
                        modelKey = modelKey,
                        modelType = modelType,
                        favorite = isFavorited,
                        notifications = emptyList(),
                    )
                } catch (e: Exception) {
                    android.util.Log.w("MyTBAViewModel", "Failed to remove subscription", e)
                }
            }
        }

        fun refresh() {
            viewModelScope.launch {
                if (!authRepository.isSignedIn()) return@launch
                refreshing(
                    { myTBARepository.refreshFavorites() },
                    { myTBARepository.refreshSubscriptions() },
                )
            }
        }

        fun refreshTab(tab: MyTBATab) {
            viewModelScope.launch {
                if (!authRepository.isSignedIn()) return@launch
                when (tab) {
                    MyTBATab.FAVORITES -> refreshing({ myTBARepository.refreshFavorites() })
                    MyTBATab.NOTIFICATIONS ->
                        refreshing({ myTBARepository.refreshSubscriptions() })
                }
            }
        }

        fun signOut() {
            viewModelScope.launch {
                try {
                    deviceRegistrationManager.onSignOut()
                } catch (_: Exception) {
                }
                authRepository.signOut()
                myTBARepository.clearLocal()
            }
        }
    }

internal fun buildMyTBADisplayNames(
    teams: List<Team>,
    events: List<Event>,
    matches: List<Match>,
): Map<String, String> {
    val eventsByKey = events.associateBy { it.key }
    return buildMap {
        teams.forEach { team ->
            val label = team.nickname ?: team.name
            val name = if (label.isNullOrBlank()) "${team.number}" else "${team.number} - $label"
            put(team.key, name)
        }
        events.forEach { event ->
            put(event.key, "${event.year} ${event.name}")
        }
        matches.forEach { match ->
            val event = eventsByKey[match.eventKey] ?: return@forEach
            val label = match.getFullLabel(event.playoffType)
            put(match.key, "$label - ${event.year} ${event.name}")
        }
    }
}
