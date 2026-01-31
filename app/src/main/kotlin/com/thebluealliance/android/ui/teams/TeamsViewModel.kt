package com.thebluealliance.android.ui.teams

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thebluealliance.android.data.repository.TeamRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TeamsViewModel @Inject constructor(
    private val teamRepository: TeamRepository,
) : ViewModel() {

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    val uiState: StateFlow<TeamsUiState> = teamRepository.observeAllTeams()
        .map { teams ->
            if (teams.isEmpty()) TeamsUiState.Loading
            else TeamsUiState.Success(teams)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TeamsUiState.Loading)

    init {
        refreshTeams()
    }

    fun refreshTeams() {
        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                (0..19).map { page ->
                    launch { teamRepository.refreshTeamsPage(page) }
                }.forEach { it.join() }
            } catch (_: Exception) {
            } finally {
                _isRefreshing.value = false
            }
        }
    }
}
