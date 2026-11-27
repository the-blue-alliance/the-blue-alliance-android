package com.thebluealliance.android.ui.teams

import androidx.lifecycle.viewModelScope
import com.thebluealliance.android.data.remote.TbaApi
import com.thebluealliance.android.data.repository.AuthRepository
import com.thebluealliance.android.data.repository.DistrictRepository
import com.thebluealliance.android.data.repository.EventRepository
import com.thebluealliance.android.data.repository.MyTBARepository
import com.thebluealliance.android.data.repository.SeasonRepository
import com.thebluealliance.android.data.repository.TeamRepository
import com.thebluealliance.android.domain.model.Favorite
import com.thebluealliance.android.domain.model.ModelType
import com.thebluealliance.android.domain.model.Subscription
import com.thebluealliance.android.navigation.Screen
import com.thebluealliance.android.shortcuts.TBAShortcutManager
import com.thebluealliance.android.ui.common.RefreshOutcome
import com.thebluealliance.android.ui.common.RefreshableViewModel
import com.thebluealliance.android.ui.common.SeasonSelection
import com.thebluealliance.android.ui.common.UserMessageBus
import com.thebluealliance.android.ui.common.defaultSeason
import com.thebluealliance.android.ui.common.isNotFound
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel(assistedFactory = TeamDetailViewModel.Factory::class)
class TeamDetailViewModel
    @AssistedInject
    constructor(
        @Assisted val navKey: Screen.TeamDetail,
        private val teamRepository: TeamRepository,
        private val eventRepository: EventRepository,
        private val districtRepository: DistrictRepository,
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

        // Bumped on every season load, so a fetch from an earlier visit to the same season
        // (2023, 2022, then 2023 again) isn't mistaken for the current one.
        private val seasonGeneration = AtomicInteger(0)
        val selectedYear: StateFlow<Int> = selection.year

        private val teamMissing = MutableStateFlow(false)

        private val loadOutcomes = MutableStateFlow<Map<TeamDetailTab, RefreshOutcome>>(emptyMap())

        // Opening fetches the team and its season separately; say "couldn't refresh" once.
        private val refreshFailureReported = AtomicBoolean(false)

        private val _yearsParticipated = MutableStateFlow<List<Int>>(emptyList())
        val yearsParticipated: StateFlow<List<Int>> = _yearsParticipated.asStateFlow()

        /** District rankings already fetched by this screen, so revisits read the cache. */
        private val fetchedDistrictKeys = mutableSetOf<String>()

        /** The districts the selected year's events point at; rankings say which is the team's. */
        private var currentDistrictKeys: List<String> = emptyList()

        private val districtStanding: Flow<TeamDistrictStanding?> =
            selection.year.flatMapLatest { year ->
                eventRepository
                    .observeTeamEvents(teamKey, year)
                    .map { teamDistrictKeys(it, year) }
                    .distinctUntilChanged()
                    .onEach { keys ->
                        currentDistrictKeys = keys
                        refreshDistrictRankings(year, keys.filter { fetchedDistrictKeys.add(it) })
                    }.flatMapLatest { keys -> observeDistrictStanding(keys) }
            }

        private fun observeDistrictStanding(keys: List<String>): Flow<TeamDistrictStanding?> {
            if (keys.isEmpty()) return flowOf(null)
            val snapshots =
                keys.map { key ->
                    combine(
                        districtRepository.observeDistrict(key),
                        districtRepository.observeDistrictRankings(key),
                    ) { district, rankings -> DistrictRankingsSnapshot(key, district, rankings) }
                }
            return combine(snapshots) { teamDistrictStanding(teamKey, it.toList()) }
        }

        private val baseState: Flow<TeamDetailUiState> =
            combine(
                teamRepository.observeTeam(teamKey),
                selection.year.flatMapLatest { year ->
                    eventRepository.observeTeamEvents(teamKey, year)
                },
                selection.year.flatMapLatest { year ->
                    teamRepository.observeTeamMedia(teamKey, year)
                },
                teamMissing,
                loadOutcomes,
            ) { team, events, media, missing, outcomes ->
                TeamDetailUiState(
                    team = team,
                    events = events,
                    media = media,
                    teamMissing = missing,
                    loadOutcomes = outcomes,
                )
            }

        private val state: Flow<TeamDetailUiState> =
            combine(baseState, districtStanding) { base, standing ->
                base.copy(districtStanding = standing)
            }

        val uiState: StateFlow<TeamDetailUiState> =
            state.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TeamDetailUiState())

        /**
         * Fetches [keys]' rankings, plus the year's districts if a name is missing. Quietly: the Info
         * row just stays hidden if this fails, so it doesn't drive the refresh indicator.
         */
        private fun refreshDistrictRankings(
            year: Int,
            keys: List<String>,
        ) {
            if (keys.isEmpty()) return
            viewModelScope.launch {
                keys.forEach { key ->
                    launch { quietly { districtRepository.refreshDistrictRankings(key) } }
                }
                launch {
                    quietly {
                        if (keys.any { districtRepository.observeDistrict(it).first() == null }) {
                            districtRepository.refreshDistrictsForYear(year)
                        }
                    }
                }
            }
        }

        private suspend fun quietly(task: suspend () -> Unit) {
            try {
                task()
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                // The district row stays hidden.
            }
        }

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
            refreshTracked(TeamDetailTab.INFO to { refreshTeam() })
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
            refreshFailureReported.set(false)
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
                    userMessages.show("Couldn't save notification preferences")
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
            refreshFailureReported.set(false)
            val year = selection.year.value
            refreshTracked(TeamDetailTab.INFO to { refreshTeam() })
            refreshSeasonTabs(
                year,
                TeamDetailTab.EVENTS to { eventRepository.refreshTeamEvents(teamKey, year) },
                TeamDetailTab.MEDIA to { teamRepository.refreshTeamMedia(teamKey, year) },
            )
            refreshDistrictRankings(year, currentDistrictKeys)
        }

        /** Retries a tab's failed load; with no team cached, every tab failed, so retry them all. */
        fun retry(tab: TeamDetailTab) {
            if (uiState.value.team == null) refreshAll() else refreshTab(tab)
        }

        fun refreshTab(tab: TeamDetailTab) {
            refreshFailureReported.set(false)
            val year = selection.year.value
            when (tab) {
                TeamDetailTab.INFO -> {
                    refreshTracked(tab to { refreshTeam() })
                    refreshSeasonTabs(
                        year,
                        TeamDetailTab.MEDIA to { teamRepository.refreshTeamMedia(teamKey, year) },
                    )
                    refreshDistrictRankings(year, currentDistrictKeys)
                }
                TeamDetailTab.EVENTS ->
                    refreshSeasonTabs(
                        year,
                        tab to { eventRepository.refreshTeamEvents(teamKey, year) },
                    )
                TeamDetailTab.MEDIA ->
                    refreshSeasonTabs(
                        year,
                        tab to { teamRepository.refreshTeamMedia(teamKey, year) },
                    )
            }
        }

        // Records whether TBA has the team at all, so a 404 shows "not found", not a spinner.
        private suspend fun refreshTeam() {
            try {
                teamRepository.refreshTeam(teamKey)
                teamMissing.value = false
            } catch (e: Exception) {
                if (e.isNotFound()) teamMissing.value = true
                throw e
            }
        }

        private fun refreshYearData() {
            seasonGeneration.incrementAndGet()
            val year = selection.year.value
            // A new season's tabs haven't been fetched yet; the last season's outcome isn't theirs.
            loadOutcomes.update { it - TeamDetailTab.EVENTS - TeamDetailTab.MEDIA }
            refreshSeasonTabs(
                year,
                TeamDetailTab.EVENTS to { eventRepository.refreshTeamEvents(teamKey, year) },
                TeamDetailTab.MEDIA to { teamRepository.refreshTeamMedia(teamKey, year) },
            )
        }

        private fun refreshTracked(vararg tasks: Pair<TeamDetailTab?, suspend () -> Unit>) {
            refreshTabs(loadOutcomes, ::reportFailedRefresh, *tasks)
        }

        // A slow fetch for a season the user has left doesn't decide the new season's tabs.
        private fun refreshSeasonTabs(
            year: Int,
            vararg tasks: Pair<TeamDetailTab?, suspend () -> Unit>,
        ) {
            val generation = seasonGeneration.get()
            refreshTabs(
                loadOutcomes,
                ::reportFailedRefresh,
                *tasks,
                isCurrent = {
                    seasonGeneration.get() == generation && selection.year.value == year
                },
            )
        }

        // Reads the cache itself: an offline refresh can fail before the screen has collected it.
        private suspend fun reportFailedRefresh(failed: Set<TeamDetailTab>) {
            if (failed.isEmpty()) return
            val cached = state.first()
            if (failed.any { cached.hasData(it) } && !refreshFailureReported.getAndSet(true)) {
                userMessages.showRetry("Couldn't refresh team", ::refreshAll)
            }
        }

        @AssistedFactory
        interface Factory {
            fun create(navKey: Screen.TeamDetail): TeamDetailViewModel
        }
    }
