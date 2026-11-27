package com.thebluealliance.android.ui.teams

import androidx.lifecycle.viewModelScope
import com.thebluealliance.android.data.remote.TbaApi
import com.thebluealliance.android.data.repository.AuthRepository
import com.thebluealliance.android.data.repository.EventRepository
import com.thebluealliance.android.data.repository.MyTBARepository
import com.thebluealliance.android.data.repository.SeasonRepository
import com.thebluealliance.android.data.repository.TeamRepository
import com.thebluealliance.android.domain.model.Favorite
import com.thebluealliance.android.domain.model.ModelType
import com.thebluealliance.android.domain.model.Subscription
import com.thebluealliance.android.navigation.Screen
import com.thebluealliance.android.shortcuts.TBAShortcutManager
import com.thebluealliance.android.ui.common.RefreshableViewModel
import com.thebluealliance.android.ui.common.SeasonSelection
import com.thebluealliance.android.ui.common.UserMessageBus
import com.thebluealliance.android.ui.common.defaultSeason
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
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
class TeamDetailViewModel
    @AssistedInject
    constructor(
        @Assisted val navKey: Screen.TeamDetail,
        private val teamRepository: TeamRepository,
        private val eventRepository: EventRepository,
        private val myTBARepository: MyTBARepository,
        private val authRepository: AuthRepository,
        private val tbaApi: TbaApi,
        private val seasons: SeasonRepository,
        private val shortcutManager: TBAShortcutManager,
        private val userMessages: UserMessageBus,
    ) : RefreshableViewModel() {
        private val teamKey: String =
            navKey.teamKey.let { key ->
                // Deep links from thebluealliance.com use /team/177 (number only),
                // but the API/DB key format is "frc177".
                if (key.all { it.isDigit() }) "frc$key" else key
            }

        private val selection =
            SeasonSelection(Calendar.getInstance().get(Calendar.YEAR)).apply {
                // A year in the link counts as the user's pick, so later defaults leave it alone.
                navKey.year?.let(::pick)
            }
        val selectedYear: StateFlow<Int> = selection.year

        private val _yearsParticipated = MutableStateFlow<List<Int>>(emptyList())
        val yearsParticipated: StateFlow<List<Int>> = _yearsParticipated.asStateFlow()

        val uiState: StateFlow<TeamDetailUiState> =
            combine(
                teamRepository.observeTeam(teamKey),
                selection.year.flatMapLatest { year ->
                    eventRepository.observeTeamEvents(teamKey, year)
                },
                selection.year.flatMapLatest { year ->
                    teamRepository.observeTeamMedia(teamKey, year)
                },
            ) { team, events, media ->
                TeamDetailUiState(
                    team = team,
                    events = events,
                    media = media,
                )
            }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TeamDetailUiState())

        val isFavorite: StateFlow<Boolean> =
            myTBARepository
                .isFavorite(teamKey, ModelType.TEAM)
                .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

        val subscription: StateFlow<Subscription?> =
            myTBARepository
                .observeSubscription(teamKey, ModelType.TEAM)
                .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

        private val _showSignInPrompt = MutableSharedFlow<Unit>()
        val showSignInPrompt: SharedFlow<Unit> = _showSignInPrompt.asSharedFlow()

        init {
            // Year data waits for the default season, so nothing is fetched for a season the
            // picker is about to leave.
            refreshing({ teamRepository.refreshTeam(teamKey) })
            if (navKey.year != null) refreshYearData()
            loadSeasons()
        }

        private fun loadSeasons() {
            viewModelScope.launch {
                val current = async { seasons.defaultSeason() }
                val years =
                    try {
                        tbaApi.getTeamYearsParticipated(teamKey).sortedDescending()
                    } catch (e: CancellationException) {
                        throw e
                    } catch (_: Exception) {
                        null
                    }
                if (years != null) {
                    _yearsParticipated.value = years
                    openOnDefault(defaultSeason(years, current.await()))
                } else {
                    openOnDefault(current.await())
                    // Fallback: generate year range from rookieYear if available
                    buildYearsFromRookieYear(current.await())
                }
            }
        }

        /** Opens on [year] and loads its data, unless the user has already picked a year. */
        private fun openOnDefault(year: Int?) {
            val moved = year != null && selection.offerDefault(year)
            if (moved || !selection.hasUserPick) refreshYearData()
        }

        private fun buildYearsFromRookieYear(currentSeason: Int) {
            viewModelScope.launch {
                teamRepository.observeTeam(teamKey).collect { team ->
                    if (team != null && _yearsParticipated.value.isEmpty()) {
                        val rookieYear = team.rookieYear ?: return@collect
                        val maxYear = Calendar.getInstance().get(Calendar.YEAR)
                        val years = (maxYear downTo rookieYear).toList()
                        _yearsParticipated.value = years
                        val year = defaultSeason(years, currentSeason)
                        if (year != null && selection.offerDefault(year)) refreshYearData()
                    }
                }
            }
        }

        fun selectYear(year: Int) {
            selection.pick(year)
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
                } catch (_: Exception) {
                    userMessages.show("Couldn't update favorite")
                }
            }
        }

        fun updatePreferences(
            favorite: Boolean,
            notifications: List<String>,
        ) {
            viewModelScope.launch {
                try {
                    myTBARepository.updatePreferences(
                        teamKey,
                        ModelType.TEAM,
                        favorite,
                        notifications,
                    )
                } catch (_: Exception) {
                    userMessages.show("Failed to save notification preferences")
                }
            }
        }

        val canPinShortcuts: Boolean = shortcutManager.canPinShortcuts()

        fun requestPinShortcut() {
            viewModelScope.launch {
                shortcutManager.requestPinShortcut(
                    Favorite(modelKey = teamKey, modelType = ModelType.TEAM),
                )
            }
        }

        fun isSignedIn(): Boolean = authRepository.isSignedIn()

        fun refreshAll() {
            val year = selection.year.value
            refreshing(
                { teamRepository.refreshTeam(teamKey) },
                { eventRepository.refreshTeamEvents(teamKey, year) },
                { teamRepository.refreshTeamMedia(teamKey, year) },
            )
        }

        fun refreshTab(tab: TeamDetailTab) {
            val year = selection.year.value
            when (tab) {
                TeamDetailTab.INFO ->
                    refreshing(
                        { teamRepository.refreshTeam(teamKey) },
                        { teamRepository.refreshTeamMedia(teamKey, year) },
                    )
                TeamDetailTab.EVENTS ->
                    refreshing({ eventRepository.refreshTeamEvents(teamKey, year) })
                TeamDetailTab.MEDIA ->
                    refreshing({ teamRepository.refreshTeamMedia(teamKey, year) })
            }
        }

        private fun refreshYearData() {
            val year = selection.year.value
            refreshing(
                { eventRepository.refreshTeamEvents(teamKey, year) },
                { teamRepository.refreshTeamMedia(teamKey, year) },
            )
        }

        @AssistedFactory
        interface Factory {
            fun create(navKey: Screen.TeamDetail): TeamDetailViewModel
        }
    }
