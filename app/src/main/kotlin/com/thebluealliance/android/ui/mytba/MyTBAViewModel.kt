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
import com.thebluealliance.android.ui.common.RefreshOutcome
import com.thebluealliance.android.ui.common.RefreshableViewModel
import com.thebluealliance.android.ui.common.UserMessageBus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
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
        private val userMessages: UserMessageBus,
    ) : RefreshableViewModel() {
        private val favoritesOutcome = MutableStateFlow(RefreshOutcome.PENDING)
        private val subscriptionsOutcome = MutableStateFlow(RefreshOutcome.PENDING)

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
                // event_team keys are "{eventKey}_{teamKey}" — observe both halves so the row
                // resolves to a friendly "team @ event" name instead of the raw key (#1460).
                val eventTeamKeys = keysOf(ModelType.EVENT_TEAM)
                val teamKeys =
                    (
                        keysOf(
                            ModelType.TEAM,
                        ) + eventTeamKeys.map { it.substringAfter('_') }
                    ).distinct()
                val eventKeys =
                    (
                        keysOf(ModelType.EVENT) +
                            matchKeys.map { it.substringBefore('_') } +
                            eventTeamKeys.map { it.substringBefore('_') }
                    ).distinct()
                combine(
                    teamRepository.observeTeams(teamKeys),
                    eventRepository.observeEvents(eventKeys),
                    matchRepository.observeMatches(matchKeys),
                ) { teams, events, matches ->
                    buildMyTBADisplayNames(teams, events, matches, eventTeamKeys)
                }
            }

        val uiState: StateFlow<MyTBAUiState> =
            combine(
                authRepository.currentUser,
                myTBARepository.observeFavorites(),
                myTBARepository.observeSubscriptions(),
                displayNames,
                combine(favoritesOutcome, subscriptionsOutcome, ::Pair),
            ) {
                user,
                favorites,
                subscriptions,
                displayNames,
                (favoritesResult, subscriptionsResult),
                ->
                MyTBAUiState(
                    isSignedIn = user != null,
                    userName = user?.displayName,
                    userEmail = user?.email,
                    userPhotoUrl = user?.photoUrl?.toString(),
                    favorites = favorites,
                    subscriptions = subscriptions,
                    displayNames = displayNames,
                    canPinShortcuts = shortcutManager.canPinShortcuts(),
                    favoritesError =
                        "Couldn't load favorites".takeIf {
                            favorites.isEmpty() && favoritesResult == RefreshOutcome.FAILED
                        },
                    subscriptionsError =
                        "Couldn't load notifications".takeIf {
                            subscriptions.isEmpty() && subscriptionsResult == RefreshOutcome.FAILED
                        },
                )
            }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), MyTBAUiState())

        init {
            // Auto-refresh when user signs in
            viewModelScope.launch {
                authRepository.currentUser
                    .map { it != null }
                    .distinctUntilChanged()
                    .collect { signedIn ->
                        favoritesOutcome.value = RefreshOutcome.PENDING
                        subscriptionsOutcome.value = RefreshOutcome.PENDING
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
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    android.util.Log.w("MyTBAViewModel", "Failed to remove favorite", e)
                    userMessages.showRetry("Couldn't remove favorite") { removeFavorite(favorite) }
                    return@launch
                }
                userMessages.showUndo("Removed from favorites") { restoreFavorite(favorite) }
            }
        }

        private fun restoreFavorite(favorite: Favorite) {
            viewModelScope.launch {
                // An Undo left up after signing out has no account to restore to.
                if (!authRepository.isSignedIn()) return@launch
                try {
                    myTBARepository.addFavorite(favorite.modelKey, favorite.modelType)
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    android.util.Log.w("MyTBAViewModel", "Failed to restore favorite", e)
                    userMessages.showRetry(
                        "Couldn't restore favorite",
                    ) { restoreFavorite(favorite) }
                }
            }
        }

        fun removeSubscription(
            modelKey: String,
            modelType: Int,
        ) {
            viewModelScope.launch {
                val isFavorited =
                    uiState.value.favorites.any {
                        it.modelKey == modelKey && it.modelType == modelType
                    }
                val previous =
                    uiState.value.subscriptions
                        .firstOrNull { it.modelKey == modelKey && it.modelType == modelType }
                        ?.notifications
                        .orEmpty()
                try {
                    myTBARepository.updatePreferences(
                        modelKey = modelKey,
                        modelType = modelType,
                        favorite = isFavorited,
                        notifications = emptyList(),
                    )
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    android.util.Log.w("MyTBAViewModel", "Failed to remove subscription", e)
                    userMessages.showRetry("Couldn't remove notifications") {
                        removeSubscription(modelKey, modelType)
                    }
                    return@launch
                }
                if (previous.isEmpty()) {
                    userMessages.show("Removed from notifications")
                } else {
                    userMessages.showUndo("Removed from notifications") {
                        restoreSubscription(modelKey, modelType, previous)
                    }
                }
            }
        }

        private fun restoreSubscription(
            modelKey: String,
            modelType: Int,
            notifications: List<String>,
        ) {
            viewModelScope.launch {
                if (!authRepository.isSignedIn()) return@launch
                try {
                    // Read now, so restoring notifications keeps a favorite change made since.
                    val favorite =
                        myTBARepository.observeFavorites().first().any {
                            it.modelKey == modelKey && it.modelType == modelType
                        }
                    myTBARepository.updatePreferences(modelKey, modelType, favorite, notifications)
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    android.util.Log.w("MyTBAViewModel", "Failed to restore subscription", e)
                    userMessages.showRetry("Couldn't restore notifications") {
                        restoreSubscription(modelKey, modelType, notifications)
                    }
                }
            }
        }

        fun refresh() {
            viewModelScope.launch {
                if (!authRepository.isSignedIn()) return@launch
                refreshingReported(
                    { reportFailedRefresh(MyTBATab.entries, ::refresh) },
                    { tracked(favoritesOutcome) { myTBARepository.refreshFavorites() } },
                    { tracked(subscriptionsOutcome) { myTBARepository.refreshSubscriptions() } },
                )
            }
        }

        fun refreshTab(tab: MyTBATab) {
            viewModelScope.launch {
                if (!authRepository.isSignedIn()) return@launch
                val report = suspend { reportFailedRefresh(listOf(tab)) { refreshTab(tab) } }
                when (tab) {
                    MyTBATab.FAVORITES ->
                        refreshingReported(report, {
                            tracked(favoritesOutcome) { myTBARepository.refreshFavorites() }
                        })
                    MyTBATab.NOTIFICATIONS ->
                        refreshingReported(report, {
                            tracked(subscriptionsOutcome) { myTBARepository.refreshSubscriptions() }
                        })
                }
            }
        }

        // A tab that failed with nothing cached shows its error screen instead.
        private suspend fun reportFailedRefresh(
            tabs: List<MyTBATab>,
            retry: () -> Unit,
        ) {
            val failedOverCache =
                tabs.any { tab ->
                    when (tab) {
                        MyTBATab.FAVORITES ->
                            favoritesOutcome.value == RefreshOutcome.FAILED &&
                                myTBARepository.observeFavorites().first().isNotEmpty()
                        MyTBATab.NOTIFICATIONS ->
                            subscriptionsOutcome.value == RefreshOutcome.FAILED &&
                                myTBARepository.observeSubscriptions().first().isNotEmpty()
                    }
                }
            if (failedOverCache) userMessages.showRetry("Couldn't refresh myTBA", retry)
        }

        private suspend fun tracked(
            outcome: MutableStateFlow<RefreshOutcome>,
            task: suspend () -> Unit,
        ) {
            try {
                task()
                outcome.value = RefreshOutcome.SUCCEEDED
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                outcome.value = RefreshOutcome.FAILED
                throw e
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
    eventTeamKeys: List<String> = emptyList(),
): Map<String, String> {
    val eventsByKey = events.associateBy { it.key }
    val teamsByKey = teams.associateBy { it.key }
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
        // event_team ("{eventKey}_{teamKey}") → "2471 @ 2024 <Event>", falling back to the
        // parsed key parts when the team/event aren't cached locally (#1460).
        eventTeamKeys.forEach { key ->
            val eventKey = key.substringBefore('_')
            val teamKey = key.substringAfter('_')
            val teamLabel = teamsByKey[teamKey]?.number?.toString() ?: teamKey.removePrefix("frc")
            val eventLabel = eventsByKey[eventKey]?.let { "${it.year} ${it.name}" } ?: eventKey
            put(key, "$teamLabel @ $eventLabel")
        }
    }
}
