package com.thebluealliance.android.ui.teams

import androidx.lifecycle.viewModelScope
import com.thebluealliance.android.data.repository.AuthRepository
import com.thebluealliance.android.data.repository.MyTBARepository
import com.thebluealliance.android.data.repository.TeamRepository
import com.thebluealliance.android.domain.model.ModelType
import com.thebluealliance.android.domain.model.Team
import com.thebluealliance.android.ui.common.RefreshableViewModel
import com.thebluealliance.android.ui.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
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
        val uiState: StateFlow<UiState<TeamsData>> =
            combine(
                teamRepository.observeAllTeams(),
                myTBARepository.observeFavorites(),
            ) { teams, favorites ->
                val state: UiState<TeamsData> =
                    if (teams.isEmpty()) {
                        UiState.Loading
                    } else {
                        val favoriteTeamKeys =
                            favorites
                                .filter { it.modelType == ModelType.TEAM }
                                .map { it.modelKey }
                                .toSet()
                        UiState.Success(TeamsData(teams, favoriteTeamKeys))
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
            val pages: List<suspend () -> Unit> =
                (0..19).map { page -> { teamRepository.refreshTeamsPage(page) } }
            refreshing(*pages.toTypedArray())
        }
    }
