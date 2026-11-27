package com.thebluealliance.android.ui.events.detail

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.thebluealliance.android.data.repository.AuthRepository
import com.thebluealliance.android.data.repository.DistrictRepository
import com.thebluealliance.android.data.repository.EventRepository
import com.thebluealliance.android.data.repository.MatchRepository
import com.thebluealliance.android.data.repository.MyTBARepository
import com.thebluealliance.android.data.repository.RegionalAdvancementRepository
import com.thebluealliance.android.data.repository.TeamRepository
import com.thebluealliance.android.domain.model.CmpAdvancement
import com.thebluealliance.android.domain.model.Favorite
import com.thebluealliance.android.domain.model.ModelType
import com.thebluealliance.android.domain.model.Subscription
import com.thebluealliance.android.domain.model.withPlayoffAlliances
import com.thebluealliance.android.navigation.Screen
import com.thebluealliance.android.shortcuts.TBAShortcutManager
import com.thebluealliance.android.ui.common.RefreshOutcome
import com.thebluealliance.android.ui.common.RefreshableViewModel
import com.thebluealliance.android.ui.common.UserMessageBus
import com.thebluealliance.android.ui.common.isFetchFailure
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel(assistedFactory = EventDetailViewModel.Factory::class)
class EventDetailViewModel
    @AssistedInject
    constructor(
        @Assisted val navKey: Screen.EventDetail,
        private val eventRepository: EventRepository,
        private val teamRepository: TeamRepository,
        private val matchRepository: MatchRepository,
        private val districtRepository: DistrictRepository,
        private val regionalAdvancementRepository: RegionalAdvancementRepository,
        private val myTBARepository: MyTBARepository,
        private val authRepository: AuthRepository,
        private val shortcutManager: TBAShortcutManager,
        private val userMessages: UserMessageBus,
    ) : RefreshableViewModel() {
        private val eventKey: String = navKey.eventKey
        private val eventYear: Int = eventKey.take(4).toIntOrNull() ?: 0

        private val regionalCmpAdvancementByTeam =
            MutableStateFlow<Map<String, CmpAdvancement>>(emptyMap())

        private val loadOutcomes =
            MutableStateFlow<Map<EventDetailTab, RefreshOutcome>>(emptyMap())

        private val teamKeysFlow = teamRepository.observeEventTeamKeys(eventKey)

        private val teamsFlow =
            teamKeysFlow.flatMapLatest { keys ->
                if (keys.isEmpty()) flowOf(emptyList()) else teamRepository.observeTeams(keys)
            }

        private val state: Flow<EventDetailUiState> =
            eventRepository
                .observeEvent(eventKey)
                .flatMapLatest { event ->
                    val coreBaseFlow =
                        combine(
                            flowOf(event),
                            teamsFlow,
                            matchRepository.observeEventMatches(eventKey),
                            eventRepository.observeEventRankings(eventKey),
                        ) { currentEvent, teams, matches, rankings ->
                            EventCoreBaseState(currentEvent, teams, matches, rankings)
                        }

                    val coreFlow =
                        combine(
                            coreBaseFlow,
                            eventRepository.observeEventRankingSortOrders(eventKey),
                            eventRepository.observeEventRankingExtraStatsInfo(eventKey),
                        ) { coreBase, rankingSortOrders, rankingExtraStatsInfo ->
                            EventCoreState(
                                event = coreBase.event,
                                teams = coreBase.teams,
                                matches = coreBase.matches,
                                rankings = coreBase.rankings,
                                rankingSortOrders = rankingSortOrders,
                                rankingExtraStatsInfo = rankingExtraStatsInfo,
                            )
                        }

                    val extrasLeftFlow =
                        combine(
                            eventRepository.observeEventAlliances(eventKey),
                            eventRepository.observeEventAwards(eventKey),
                        ) { alliances, awards ->
                            Pair(alliances, awards)
                        }

                    val extrasRightFlow =
                        combine(
                            if (event?.district !=
                                null
                            ) {
                                eventRepository.observeEventDistrictPoints(eventKey)
                            } else {
                                eventRepository.observeEventRegionalPoints(eventKey)
                            },
                            eventRepository.observeEventOPRs(eventKey),
                            eventRepository.observeEventCOPRs(eventKey),
                            regionalCmpAdvancementByTeam,
                        ) { advancementPoints, oprs, coprs, regionalCmpAdvancementByTeam ->
                            EventExtrasRightState(
                                advancementPoints,
                                oprs,
                                coprs,
                                regionalCmpAdvancementByTeam,
                            )
                        }

                    val extrasFlow =
                        combine(extrasLeftFlow, extrasRightFlow) { left, right ->
                            EventExtrasState(
                                alliances = left.first,
                                awards = left.second,
                                advancementPoints = right.advancementPoints,
                                oprs = right.oprs,
                                coprs = right.coprs,
                                regionalCmpAdvancementByTeam = right.regionalCmpAdvancementByTeam,
                            )
                        }

                    combine(coreFlow, extrasFlow) { core, extras ->
                        EventDetailUiState(
                            event = core.event,
                            teams = core.teams,
                            matches =
                                core.matches.map {
                                    it.withPlayoffAlliances(
                                        extras.alliances,
                                    )
                                },
                            rankings = core.rankings,
                            rankingSortOrders = core.rankingSortOrders,
                            rankingExtraStatsInfo = core.rankingExtraStatsInfo,
                            alliances = extras.alliances,
                            awards = extras.awards,
                            advancementPoints = extras.advancementPoints,
                            oprs = extras.oprs,
                            coprs = extras.coprs,
                            insights = null,
                            regionalCmpAdvancementByTeam = extras.regionalCmpAdvancementByTeam,
                        )
                    }
                }.flatMapLatest { baseState ->
                    combine(
                        eventRepository.observeEventInsights(eventKey),
                        baseState.event?.district?.let { districtRepository.observeDistrict(it) }
                            ?: flowOf(null),
                        eventRepository.observeEventPitLocations(eventKey),
                        myTBARepository.observeFavorites(),
                    ) { insights, district, pitLocations, favorites ->
                        val eventTeamKeys = baseState.teams?.map { it.key }?.toSet() ?: emptySet()
                        val favTeamKeys =
                            favorites
                                .filter {
                                    it.modelType == ModelType.TEAM && it.modelKey in eventTeamKeys
                                }.map { it.modelKey }
                        baseState.copy(
                            insights = insights,
                            districtDisplayName = district?.displayName,
                            pitLocations = pitLocations,
                            favoriteTeamKeysAtEvent = favTeamKeys,
                        )
                    }
                }.combine(loadOutcomes) { state, outcomes -> state.copy(loadOutcomes = outcomes) }

        val uiState: StateFlow<EventDetailUiState> =
            state.stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                EventDetailUiState(),
            )

        val isFavorite: StateFlow<Boolean> =
            myTBARepository
                .isFavorite(eventKey, ModelType.EVENT)
                .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

        val subscription: StateFlow<Subscription?> =
            myTBARepository
                .observeSubscription(eventKey, ModelType.EVENT)
                .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

        private val _showSignInPrompt = MutableSharedFlow<Unit>()
        val showSignInPrompt: SharedFlow<Unit> = _showSignInPrompt.asSharedFlow()

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
                        eventKey,
                        ModelType.EVENT,
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
                    Favorite(modelKey = eventKey, modelType = ModelType.EVENT),
                )
            }
        }

        fun isSignedIn(): Boolean = authRepository.isSignedIn()

        fun refreshAll() {
            refreshTracked(
                EventDetailTab.INFO to {
                    eventRepository.refreshEvent(eventKey)
                    refreshRegionalCmpAdvancementLogged()
                },
                EventDetailTab.TEAMS to { teamRepository.refreshEventTeams(eventKey) },
                EventDetailTab.MATCHES to { matchRepository.refreshEventMatches(eventKey) },
                EventDetailTab.RANKINGS to { eventRepository.refreshEventRankings(eventKey) },
                EventDetailTab.ALLIANCES to { eventRepository.refreshEventAlliances(eventKey) },
                EventDetailTab.AWARDS to { eventRepository.refreshEventAwards(eventKey) },
                EventDetailTab.ADVANCEMENT_POINTS to {
                    eventRepository.refreshEventDistrictPoints(eventKey)
                },
                EventDetailTab.ADVANCEMENT_POINTS to {
                    eventRepository.refreshEventRegionalPoints(eventKey)
                },
                EventDetailTab.INSIGHTS to { eventRepository.refreshEventOPRs(eventKey) },
                EventDetailTab.INSIGHTS to { eventRepository.refreshEventCOPRs(eventKey) },
                EventDetailTab.INSIGHTS to { eventRepository.refreshEventInsights(eventKey) },
                null to { districtRepository.refreshDistrictsForYear(eventYear) },
                null to { eventRepository.refreshEventPitLocations(eventKey) },
            )
        }

        /** Retries a tab's failed load; with no event cached, every tab failed, so retry them all. */
        fun retry(tab: EventDetailTab) {
            if (uiState.value.event == null) refreshAll() else refreshTab(tab)
        }

        fun refreshTab(tab: EventDetailTab) {
            when (tab) {
                EventDetailTab.INFO ->
                    refreshTracked(
                        tab to { eventRepository.refreshEvent(eventKey) },
                        null to { districtRepository.refreshDistrictsForYear(eventYear) },
                    )
                EventDetailTab.TEAMS ->
                    refreshTracked(
                        tab to { teamRepository.refreshEventTeams(eventKey) },
                        null to { eventRepository.refreshEventPitLocations(eventKey) },
                    )
                EventDetailTab.RANKINGS ->
                    refreshTracked(tab to { eventRepository.refreshEventRankings(eventKey) })
                EventDetailTab.MATCHES ->
                    refreshTracked(tab to { matchRepository.refreshEventMatches(eventKey) })
                EventDetailTab.ALLIANCES ->
                    refreshTracked(tab to { eventRepository.refreshEventAlliances(eventKey) })
                EventDetailTab.INSIGHTS ->
                    refreshTracked(
                        tab to { eventRepository.refreshEventOPRs(eventKey) },
                        tab to { eventRepository.refreshEventCOPRs(eventKey) },
                        tab to { eventRepository.refreshEventInsights(eventKey) },
                    )
                EventDetailTab.ADVANCEMENT_POINTS ->
                    refreshTracked(
                        tab to { eventRepository.refreshEventDistrictPoints(eventKey) },
                        tab to { eventRepository.refreshEventRegionalPoints(eventKey) },
                        null to { refreshRegionalCmpAdvancement(forceRefresh = true) },
                    )
                EventDetailTab.AWARDS ->
                    refreshTracked(tab to { eventRepository.refreshEventAwards(eventKey) })
            }
        }

        /**
         * Runs [tasks] like [refreshing], recording each tab's outcome so an empty tab can tell
         * "failed" from "none", and reporting a failed refresh over cached data once it ends.
         * A null tab marks a supporting fetch that no tab's state depends on.
         */
        private fun refreshTracked(vararg tasks: Pair<EventDetailTab?, suspend () -> Unit>) {
            val failed = ConcurrentHashMap.newKeySet<EventDetailTab>()
            val remaining = AtomicInteger(tasks.size)
            val tracked: List<suspend () -> Unit> =
                tasks.map { (tab, task) ->
                    {
                        try {
                            task()
                            if (tab != null) markSucceeded(tab, failed)
                        } catch (e: CancellationException) {
                            throw e
                        } catch (e: Exception) {
                            if (tab != null) {
                                if (e.isFetchFailure()) {
                                    failed += tab
                                    loadOutcomes.update { it + (tab to RefreshOutcome.FAILED) }
                                } else {
                                    // TBA answered with nothing for this event: empty, not failed.
                                    markSucceeded(tab, failed)
                                }
                            }
                            throw e
                        } finally {
                            if (remaining.decrementAndGet() == 0) reportFailedRefresh(failed)
                        }
                    }
                }
            refreshing(*tracked.toTypedArray())
        }

        private fun markSucceeded(
            tab: EventDetailTab,
            failed: Set<EventDetailTab>,
        ) {
            loadOutcomes.update {
                if (tab in
                    failed
                ) {
                    it
                } else {
                    it + (tab to RefreshOutcome.SUCCEEDED)
                }
            }
        }

        // Reads the cache itself: an offline refresh can fail before the screen has collected it.
        private suspend fun reportFailedRefresh(failed: Set<EventDetailTab>) {
            if (failed.isEmpty()) return
            val cached = state.first()
            if (failed.any { cached.hasData(it) }) userMessages.show("Couldn't refresh event")
        }

        // Advancement needs the stored event, so it follows it; its failure isn't Info's.
        private suspend fun refreshRegionalCmpAdvancementLogged() {
            try {
                refreshRegionalCmpAdvancement()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.w(TAG, "regional advancement refresh failed", e)
            }
        }

        // Regional advancement is a whole-season payload that only applies to non-district
        // events, so skip it entirely when the event belongs to a district.
        private suspend fun refreshRegionalCmpAdvancement(forceRefresh: Boolean = false) {
            val event = eventRepository.observeEvent(eventKey).first() ?: return
            if (event.district != null) return
            regionalCmpAdvancementByTeam.value =
                regionalAdvancementRepository.getCmpAdvancementByTeam(eventYear, forceRefresh)
        }

        @AssistedFactory
        interface Factory {
            fun create(navKey: Screen.EventDetail): EventDetailViewModel
        }

        private companion object {
            const val TAG = "EventDetailViewModel"
        }
    }

private data class EventCoreBaseState(
    val event: com.thebluealliance.android.domain.model.Event?,
    val teams: List<com.thebluealliance.android.domain.model.Team>,
    val matches: List<com.thebluealliance.android.domain.model.Match>,
    val rankings: List<com.thebluealliance.android.domain.model.Ranking>,
)

private data class EventCoreState(
    val event: com.thebluealliance.android.domain.model.Event?,
    val teams: List<com.thebluealliance.android.domain.model.Team>,
    val matches: List<com.thebluealliance.android.domain.model.Match>,
    val rankings: List<com.thebluealliance.android.domain.model.Ranking>,
    val rankingSortOrders: List<com.thebluealliance.android.domain.model.RankingSortOrder>,
    val rankingExtraStatsInfo: List<com.thebluealliance.android.domain.model.RankingSortOrder>,
)

private data class EventExtrasState(
    val alliances: List<com.thebluealliance.android.domain.model.Alliance>,
    val awards: List<com.thebluealliance.android.domain.model.Award>,
    val advancementPoints: List<com.thebluealliance.android.domain.model.EventAdvancementPoints>,
    val oprs: com.thebluealliance.android.domain.model.EventOPRs?,
    val coprs: com.thebluealliance.android.domain.model.EventCOPRs?,
    val regionalCmpAdvancementByTeam: Map<String, CmpAdvancement>,
)

private data class EventExtrasRightState(
    val advancementPoints: List<com.thebluealliance.android.domain.model.EventAdvancementPoints>,
    val oprs: com.thebluealliance.android.domain.model.EventOPRs?,
    val coprs: com.thebluealliance.android.domain.model.EventCOPRs?,
    val regionalCmpAdvancementByTeam: Map<String, CmpAdvancement>,
)
