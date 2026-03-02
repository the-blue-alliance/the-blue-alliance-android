package com.thebluealliance.android.ui.events.detail.tabs

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.thebluealliance.android.domain.model.Team
import com.thebluealliance.android.ui.common.EmptyBox
import com.thebluealliance.android.ui.common.LoadingBox
import com.thebluealliance.android.ui.components.TeamRow

@Composable
fun EventTeamsTab(
    teams: List<Team>?,
    onNavigateToTeam: (String) -> Unit,
    innerPadding: PaddingValues = PaddingValues.Zero,
) {
    if (teams == null) {
        LoadingBox(
            modifier = Modifier.padding(innerPadding)
        )
        return
    }
    if (teams.isEmpty()) {
        EmptyBox(
            modifier = Modifier.padding(innerPadding),
            message = "No teams"
        )
        return
    }
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = innerPadding,
    ) {
        items(teams, key = { it.key }) { team ->
            TeamRow(team = team, onClick = { onNavigateToTeam(team.key) })
        }
    }
}

