package com.thebluealliance.android.ui.teams

import com.thebluealliance.android.domain.model.Team

sealed interface TeamsUiState {
    data object Loading : TeamsUiState
    data class Success(
        val teams: List<Team>,
        val favoriteTeamKeys: Set<String> = emptySet(),
    ) : TeamsUiState
}
