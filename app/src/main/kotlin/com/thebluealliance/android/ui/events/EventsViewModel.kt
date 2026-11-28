package com.thebluealliance.android.ui.events

import androidx.lifecycle.viewModelScope
import com.thebluealliance.android.data.repository.AuthRepository
import com.thebluealliance.android.data.repository.DistrictRepository
import com.thebluealliance.android.data.repository.EventRepository
import com.thebluealliance.android.data.repository.MyTBARepository
import com.thebluealliance.android.data.repository.SeasonRepository
import com.thebluealliance.android.domain.model.ModelType
import com.thebluealliance.android.navigation.Screen
import com.thebluealliance.android.ui.common.RefreshOutcome
import com.thebluealliance.android.ui.common.RefreshableViewModel
import com.thebluealliance.android.ui.common.SeasonSelection
import com.thebluealliance.android.ui.common.UiState
import com.thebluealliance.android.ui.common.UserMessageBus
import com.thebluealliance.android.ui.common.isFetchFailure
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Year

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
@HiltViewModel(assistedFactory = EventsViewModel.Factory::class)
class EventsViewModel
    @AssistedInject
    constructor(
        @Assisted navKey: Screen.Events,
        private val eventRepository: EventRepository,
        private val districtRepository: DistrictRepository,
        private val seasons: SeasonRepository,
        private val myTBARepository: MyTBARepository,
        private val authRepository: AuthRepository,
        private val userMessages: UserMessageBus,
    ) : RefreshableViewModel() {
        private val _maxYear = MutableStateFlow(Year.now().value)
        val maxYear: StateFlow<Int> = _maxYear.asStateFlow()

        // Without a year in the link, the list waits for TBA's current season, like every picker.
        private val selection = SeasonSelection(navKey.year ?: Year.now().value)
        val selectedYear: StateFlow<Int> = selection.year

        private val yearResolved = MutableStateFlow(navKey.year != null)

        // Each season's last fetch outcome. Keyed by season, not reset when the screen
        // subscribes, so a fetch that finished first still counts.
        private val outcomes = MutableStateFlow<Map<Int, RefreshOutcome>>(emptyMap())

        val uiState: StateFlow<UiState<EventsData>> =
            combine(selection.year, yearResolved) { year, resolved -> year.takeIf { resolved } }
                .flatMapLatest { year ->
                    if (year ==
                        null
                    ) {
                        return@flatMapLatest flowOf<UiState<EventsData>>(UiState.Loading)
                    }
                    combine(
                        eventRepository.observeEventsForYear(year),
                        districtRepository.observeDistrictsForYear(year),
                        outcomes,
                        myTBARepository.observeFavorites(),
                    ) { events, districts, outcomes, favorites ->
                        val state: UiState<EventsData> =
                            when {
                                events.isEmpty() && outcomes[year] == RefreshOutcome.FAILED ->
                                    UiState.Error("Couldn't load events")
                                events.isEmpty() && outcomes[year] == RefreshOutcome.SUCCEEDED ->
                                    UiState.Empty
                                events.isEmpty() -> UiState.Loading
                                else -> {
                                    val districtNames =
                                        buildMap {
                                            districts.forEach {
                                                put(it.key.lowercase(), it.displayName)
                                                put(it.abbreviation.lowercase(), it.displayName)
                                            }
                                        }
                                    val sections =
                                        buildEventSections(events, districtNames = districtNames)
                                    val favoriteEventKeys =
                                        favorites
                                            .filter { it.modelType == ModelType.EVENT }
                                            .map { it.modelKey }
                                            .toSet()
                                    UiState.Success(
                                        EventsData(sections, favoriteEventKeys, districtNames),
                                    )
                                }
                            }
                        state
                    }
                }.stateIn(
                    viewModelScope,
                    SharingStarted.WhileSubscribed(5000),
                    UiState.Loading,
                )

        init {
            if (navKey.year == null) {
                openOnDefaultSeason()
            } else {
                refreshEvents()
                fetchMaxYear()
            }
            refreshFavorites()
        }

        // Both bounds come from one status fetch, which isn't cached when it fails. Resolving the
        // year first keeps a stalled network from holding the list for two timeouts.
        private fun openOnDefaultSeason() {
            viewModelScope.launch {
                val year = seasons.defaultSeason()
                if (!selection.hasUserPick) {
                    selection.offerDefault(year)
                    _maxYear.value = maxOf(_maxYear.value, year)
                    yearResolved.value = true
                    refreshEvents()
                }
                fetchMaxYear()
            }
        }

        private fun refreshFavorites() {
            viewModelScope.launch {
                if (!authRepository.isSignedIn()) return@launch
                try {
                    myTBARepository.refreshFavorites()
                } catch (_: Exception) {
                }
            }
        }

        private fun fetchMaxYear() {
            viewModelScope.launch {
                // Falls back to the calendar year (already set) without a status.
                seasons.maxSeason()?.let { _maxYear.value = maxOf(it, selection.year.value) }
            }
        }

        fun selectYear(year: Int) {
            selection.pick(year)
            yearResolved.value = true
            refreshEvents()
        }

        fun refreshEvents() {
            val year = selection.year.value
            refreshingReported({ reportFailedRefresh(year) }, {
                try {
                    eventRepository.refreshEventsForYear(year)
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    // Like refreshTabs: a 404 or empty answer means none, not a failure.
                    val outcome =
                        if (e.isFetchFailure()) RefreshOutcome.FAILED else RefreshOutcome.SUCCEEDED
                    outcomes.update { it + (year to outcome) }
                    throw e
                }
                outcomes.update { it + (year to RefreshOutcome.SUCCEEDED) }
                districtRepository.refreshDistrictsForYear(year)
            })
        }

        // A season the user has left doesn't report over the new one.
        private suspend fun reportFailedRefresh(year: Int) {
            if (year != selection.year.value) return
            if (eventRepository.observeEventsForYear(year).first().isNotEmpty()) {
                userMessages.showRetry("Couldn't refresh events", ::refreshEvents)
            }
        }

        @AssistedFactory
        interface Factory {
            fun create(navKey: Screen.Events): EventsViewModel
        }
    }
