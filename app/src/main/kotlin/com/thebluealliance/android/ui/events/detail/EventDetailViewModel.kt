package com.thebluealliance.android.ui.events.detail

import androidx.lifecycle.viewModelScope
import com.thebluealliance.android.data.repository.AuthRepository
import com.thebluealliance.android.data.repository.DistrictRepository
import com.thebluealliance.android.data.repository.EventRepository
import com.thebluealliance.android.data.repository.MatchRepository
import com.thebluealliance.android.data.repository.MyTBARepository
import com.thebluealliance.android.data.repository.TeamRepository
import com.thebluealliance.android.domain.model.CmpAdvancement
import com.thebluealliance.android.domain.model.Favorite
import com.thebluealliance.android.domain.model.ModelType
import com.thebluealliance.android.domain.model.Subscription
import com.thebluealliance.android.domain.model.withPlayoffAlliances
import com.thebluealliance.android.navigation.Screen
import com.thebluealliance.android.shortcuts.TBAShortcutManager
import com.thebluealliance.android.ui.common.RefreshableViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

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
        private val myTBARepository: MyTBARepository,
        private val authRepository: AuthRepository,
        private val shortcutManager: TBAShortcutManager,
    ) : RefreshableViewModel() {
        private val eventKey: String = navKey.eventKey
        private val eventYear: Int = eventKey.take(4).toIntOrNull() ?: 0

        private val regionalCmpAdvancementByTeam =
            MutableStateFlow<Map<String, CmpAdvancement>>(emptyMap())

        private val teamKeysFlow = teamRepository.observeEventTeamKeys(eventKey)

        private val teamsFlow =
            teamKeysFlow.flatMapLatest { keys ->
                if (keys.isEmpty()) flowOf(emptyList()) else teamRepository.observeTeams(keys)
            }

        val uiState: StateFlow<EventDetailUiState> =
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
                }.stateIn(
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
                } catch (_: Exception) {
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
                    _userMessage.emit("Failed to save notification preferences")
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
            refreshing(
                { eventRepository.refreshEvent(eventKey) },
                { teamRepository.refreshEventTeams(eventKey) },
                { matchRepository.refreshEventMatches(eventKey) },
                { eventRepository.refreshEventRankings(eventKey) },
                { eventRepository.refreshEventAlliances(eventKey) },
                { eventRepository.refreshEventAwards(eventKey) },
                { eventRepository.refreshEventDistrictPoints(eventKey) },
                { eventRepository.refreshEventRegionalPoints(eventKey) },
                {
                    regionalCmpAdvancementByTeam.value =
                        eventRepository.fetchRegionalCmpAdvancementByTeam(eventYear)
                },
                { eventRepository.refreshEventOPRs(eventKey) },
                { eventRepository.refreshEventCOPRs(eventKey) },
                { eventRepository.refreshEventInsights(eventKey) },
                { districtRepository.refreshDistrictsForYear(eventYear) },
                { eventRepository.refreshEventPitLocations(eventKey) },
            )
        }

        fun refreshTab(tab: EventDetailTab) {
            when (tab) {
                EventDetailTab.INFO ->
                    refreshing(
                        { eventRepository.refreshEvent(eventKey) },
                        { districtRepository.refreshDistrictsForYear(eventYear) },
                    )
                EventDetailTab.TEAMS ->
                    refreshing(
                        { teamRepository.refreshEventTeams(eventKey) },
                        { eventRepository.refreshEventPitLocations(eventKey) },
                    )
                EventDetailTab.RANKINGS ->
                    refreshing({ eventRepository.refreshEventRankings(eventKey) })
                EventDetailTab.MATCHES ->
                    refreshing({ matchRepository.refreshEventMatches(eventKey) })
                EventDetailTab.ALLIANCES ->
                    refreshing({ eventRepository.refreshEventAlliances(eventKey) })
                EventDetailTab.INSIGHTS ->
                    refreshing(
                        { eventRepository.refreshEventOPRs(eventKey) },
                        { eventRepository.refreshEventCOPRs(eventKey) },
                        { eventRepository.refreshEventInsights(eventKey) },
                    )
                EventDetailTab.ADVANCEMENT_POINTS ->
                    refreshing(
                        { eventRepository.refreshEventDistrictPoints(eventKey) },
                        { eventRepository.refreshEventRegionalPoints(eventKey) },
                        {
                            regionalCmpAdvancementByTeam.value =
                                eventRepository.fetchRegionalCmpAdvancementByTeam(eventYear)
                        },
                    )
                EventDetailTab.AWARDS ->
                    refreshing({ eventRepository.refreshEventAwards(eventKey) })
            }
        }

        @AssistedFactory
        interface Factory {
            fun create(navKey: Screen.EventDetail): EventDetailViewModel
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
