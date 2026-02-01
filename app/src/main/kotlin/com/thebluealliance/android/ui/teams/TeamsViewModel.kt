package com.thebluealliance.android.ui.teams

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thebluealliance.android.data.repository.MyTBARepository
import com.thebluealliance.android.data.repository.TeamRepository
import com.thebluealliance.android.domain.model.ModelType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TeamsViewModel @Inject constructor(
    private val teamRepository: TeamRepository,
    private val myTBARepository: MyTBARepository,
) : ViewModel() {

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    val uiState: StateFlow<TeamsUiState> = combine(
        teamRepository.observeAllTeams(),
        myTBARepository.observeFavorites(),
    ) { teams, favorites ->
        val favoriteTeamKeys = favorites
            .filter { it.modelType == ModelType.TEAM }
            .map { it.modelKey }
            .toSet()
        TeamsUiState.Success(teams = teams, favoriteTeamKeys = favoriteTeamKeys)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TeamsUiState.Loading)

    init {
        refreshTeams()
    }

    fun refreshTeams() {
        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                (0..19).map { page ->
                    launch { try { teamRepository.refreshTeamsPage(page) } catch (_: Exception) {} }
                }.forEach { it.join() }
            } finally {
                _isRefreshing.value = false
            }
        }
    }
}
