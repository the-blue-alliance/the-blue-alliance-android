package com.thebluealliance.android.ui.events.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thebluealliance.android.navigation.Screen
import com.thebluealliance.android.data.repository.AuthRepository
import com.thebluealliance.android.data.repository.DistrictRepository
import com.thebluealliance.android.data.repository.EventRepository
import com.thebluealliance.android.data.repository.MatchRepository
import com.thebluealliance.android.data.repository.MyTBARepository
import com.thebluealliance.android.data.repository.TeamRepository
import com.thebluealliance.android.domain.model.Favorite
import com.thebluealliance.android.domain.model.ModelType
import com.thebluealliance.android.domain.model.Subscription
import com.thebluealliance.android.shortcuts.TBAShortcutManager
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
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel(assistedFactory = EventDetailViewModel.Factory::class)
class EventDetailViewModel @AssistedInject constructor(
    @Assisted val navKey: Screen.EventDetail,
    private val eventRepository: EventRepository,
    private val teamRepository: TeamRepository,
    private val matchRepository: MatchRepository,
    private val districtRepository: DistrictRepository,
    private val myTBARepository: MyTBARepository,
    private val authRepository: AuthRepository,
    private val shortcutManager: TBAShortcutManager,
) : ViewModel() {

    private val eventKey: String = navKey.eventKey
    private val eventYear: Int = eventKey.take(4).toIntOrNull() ?: 0

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _pitLocations = MutableStateFlow<Map<String, String>>(emptyMap())

    private val teamKeysFlow = teamRepository.observeEventTeamKeys(eventKey)

    private val teamsFlow = teamKeysFlow.flatMapLatest { keys ->
        if (keys.isEmpty()) flowOf(emptyList()) else teamRepository.observeTeams(keys)
    }

    val uiState: StateFlow<EventDetailUiState> = eventRepository.observeEvent(eventKey)
        .flatMapLatest { event ->
            val coreBaseFlow = combine(
                flowOf(event),
                teamsFlow,
                matchRepository.observeEventMatches(eventKey),
                eventRepository.observeEventRankings(eventKey),
            ) { currentEvent, teams, matches, rankings ->
                EventCoreBaseState(currentEvent, teams, matches, rankings)
            }

            val coreFlow = combine(
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

            val extrasLeftFlow = combine(
                eventRepository.observeEventAlliances(eventKey),
                eventRepository.observeEventAwards(eventKey),
            ) { alliances, awards ->
                Pair(alliances, awards)
            }

            val extrasRightFlow = combine(
                if (event?.district != null) eventRepository.observeEventDistrictPoints(eventKey) else eventRepository.observeEventRegionalPoints(eventKey),
                eventRepository.observeEventOPRs(eventKey),
                eventRepository.observeEventCOPRs(eventKey),
            ) { advancementPoints, oprs, coprs ->
                Triple(advancementPoints, oprs, coprs)
            }

            val extrasFlow = combine(extrasLeftFlow, extrasRightFlow) { left, right ->
                EventExtrasState(
                    alliances = left.first,
                    awards = left.second,
                    advancementPoints = right.first,
                    oprs = right.second,
                    coprs = right.third,
                )
            }

            combine(coreFlow, extrasFlow) { core, extras ->
                EventDetailUiState(
                    event = core.event,
                    teams = core.teams,
                    matches = core.matches,
                    rankings = core.rankings,
                    rankingSortOrders = core.rankingSortOrders,
                    rankingExtraStatsInfo = core.rankingExtraStatsInfo,
                    alliances = extras.alliances,
                    awards = extras.awards,
                    advancementPoints = extras.advancementPoints,
                    oprs = extras.oprs,
                    coprs = extras.coprs,
                    insights = null,
                )
            }
        }.flatMapLatest { baseState ->
            combine(
                eventRepository.observeEventInsights(eventKey),
                baseState.event?.district?.let { districtRepository.observeDistrict(it) } ?: flowOf(null),
                _pitLocations,
            ) { insights, district, pitLocations ->
                baseState.copy(
                    insights = insights,
                    districtDisplayName = district?.displayName,
                    pitLocations = pitLocations,
                )
            }
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

    val canPinShortcuts: Boolean = shortcutManager.canPinShortcuts()

    fun requestPinShortcut() {
        viewModelScope.launch {
            shortcutManager.requestPinShortcut(
                Favorite(modelKey = eventKey, modelType = ModelType.EVENT)
            )
        }
    }

    fun isSignedIn(): Boolean = authRepository.isSignedIn()

    fun refreshAll() {
        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                coroutineScope {
                    launch { try { eventRepository.refreshEvent(eventKey) } catch (_: Exception) {} }
                    launch { try { teamRepository.refreshEventTeams(eventKey) } catch (_: Exception) {} }
                    launch { try { matchRepository.refreshEventMatches(eventKey) } catch (_: Exception) {} }
                    launch { try { eventRepository.refreshEventRankings(eventKey) } catch (_: Exception) {} }
                    launch { try { eventRepository.refreshEventAlliances(eventKey) } catch (_: Exception) {} }
                    launch { try { eventRepository.refreshEventAwards(eventKey) } catch (_: Exception) {} }
                    launch { try { eventRepository.refreshEventDistrictPoints(eventKey) } catch (_: Exception) {} }
                    launch { try { eventRepository.refreshEventRegionalPoints(eventKey) } catch (_: Exception) {} }
                    launch { try { eventRepository.refreshEventOPRs(eventKey) } catch (_: Exception) {} }
                    launch { try { eventRepository.refreshEventCOPRs(eventKey) } catch (_: Exception) {} }
                    launch { try { eventRepository.refreshEventInsights(eventKey) } catch (_: Exception) {} }
                    launch { try { districtRepository.refreshDistrictsForYear(eventYear) } catch (_: Exception) {} }
                    launch { _pitLocations.value = eventRepository.fetchEventPitLocations(eventKey) }
                }
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    fun refreshTab(tab: EventDetailTab) {
        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                coroutineScope {
                    when (tab) {
                        EventDetailTab.INFO -> {
                            launch { try { eventRepository.refreshEvent(eventKey) } catch (_: Exception) {} }
                            launch { try { districtRepository.refreshDistrictsForYear(eventYear) } catch (_: Exception) {} }
                        }
                        EventDetailTab.TEAMS -> {
                            launch { try { teamRepository.refreshEventTeams(eventKey) } catch (_: Exception) {} }
                            launch { _pitLocations.value = eventRepository.fetchEventPitLocations(eventKey) }
                        }
                        EventDetailTab.RANKINGS -> {
                            launch { try { eventRepository.refreshEventRankings(eventKey) } catch (_: Exception) {} }
                        }
                        EventDetailTab.MATCHES -> {
                            launch { try { matchRepository.refreshEventMatches(eventKey) } catch (_: Exception) {} }
                        }
                        EventDetailTab.ALLIANCES -> {
                            launch { try { eventRepository.refreshEventAlliances(eventKey) } catch (_: Exception) {} }
                        }
                        EventDetailTab.INSIGHTS -> {
                            launch { try { eventRepository.refreshEventOPRs(eventKey) } catch (_: Exception) {} }
                            launch { try { eventRepository.refreshEventCOPRs(eventKey) } catch (_: Exception) {} }
                            launch { try { eventRepository.refreshEventInsights(eventKey) } catch (_: Exception) {} }
                        }
                        EventDetailTab.ADVANCEMENT_POINTS -> {
                            launch { try { eventRepository.refreshEventDistrictPoints(eventKey) } catch (_: Exception) {} }
                            launch { try { eventRepository.refreshEventRegionalPoints(eventKey) } catch (_: Exception) {} }
                        }
                        EventDetailTab.AWARDS -> {
                            launch { try { eventRepository.refreshEventAwards(eventKey) } catch (_: Exception) {} }
                        }
                    }
                }
            } finally {
                _isRefreshing.value = false
            }
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
)
