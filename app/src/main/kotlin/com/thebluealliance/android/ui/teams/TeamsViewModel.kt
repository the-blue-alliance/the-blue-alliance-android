package com.thebluealliance.android.ui.teams

import androidx.lifecycle.viewModelScope
import com.thebluealliance.android.data.repository.AuthRepository
import com.thebluealliance.android.data.repository.MyTBARepository
import com.thebluealliance.android.data.repository.TeamRepository
import com.thebluealliance.android.domain.model.ModelType
import com.thebluealliance.android.domain.model.Team
import com.thebluealliance.android.ui.common.RefreshOutcome
import com.thebluealliance.android.ui.common.RefreshableViewModel
import com.thebluealliance.android.ui.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TeamsData(
    val teams: List<Team>,
    val favoriteTeamKeys: Set<String>,
)

@HiltViewModel
class TeamsViewModel
    @Inject
    constructor(
        private val teamRepository: TeamRepository,
        private val myTBARepository: MyTBARepository,
        private val authRepository: AuthRepository,
    ) : RefreshableViewModel() {
        private val refreshOutcome = MutableStateFlow(RefreshOutcome.PENDING)

        val uiState: StateFlow<UiState<TeamsData>> =
            combine(
                teamRepository.observeAllTeams(),
                myTBARepository.observeFavorites(),
                refreshOutcome,
            ) { teams, favorites, outcome ->
                val state: UiState<TeamsData> =
                    when {
                        teams.isNotEmpty() -> {
                            val favoriteTeamKeys =
                                favorites
                                    .filter { it.modelType == ModelType.TEAM }
                                    .map { it.modelKey }
                                    .toSet()
                            UiState.Success(TeamsData(teams, favoriteTeamKeys))
                        }
                        outcome == RefreshOutcome.PENDING -> UiState.Loading
                        outcome == RefreshOutcome.FAILED -> UiState.Error("Couldn't load teams")
                        else -> UiState.Empty
                    }
                state
            }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UiState.Loading)

        init {
            refreshTeams()
            refreshFavorites()
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

        fun refreshTeams() {
            refreshingTracked(refreshOutcome, { teamRepository.refreshAllTeams() })
        }
    }
