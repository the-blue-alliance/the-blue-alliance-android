package com.thebluealliance.android.ui.events.detail.tabs

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.thebluealliance.android.domain.model.Team
import com.thebluealliance.android.ui.common.EmptyBox
import com.thebluealliance.android.ui.common.LoadingBox
import com.thebluealliance.android.ui.components.TeamRow

@Composable
fun EventTeamsTab(teams: List<Team>?, onNavigateToTeam: (String) -> Unit) {
    if (teams == null) {
        LoadingBox()
        return
    }
    if (teams.isEmpty()) {
        EmptyBox("No teams")
        return
    }
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(teams, key = { it.key }) { team ->
            TeamRow(team = team, onClick = { onNavigateToTeam(team.key) })
        }
    }
}

