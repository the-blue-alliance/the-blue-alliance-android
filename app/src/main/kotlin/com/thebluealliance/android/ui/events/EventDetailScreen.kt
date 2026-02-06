package com.thebluealliance.android.ui.events

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.thebluealliance.android.domain.model.Alliance
import com.thebluealliance.android.domain.model.Award
import com.thebluealliance.android.domain.model.Event
import com.thebluealliance.android.domain.model.Match
import com.thebluealliance.android.domain.model.Ranking
import com.thebluealliance.android.domain.model.Team
import kotlinx.coroutines.launch

private val TABS = listOf("Info", "Teams", "Matches", "Rankings", "Alliances", "Awards")

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun EventDetailScreen(
    onNavigateBack: () -> Unit,
    onNavigateToTeam: (String) -> Unit = {},
    onNavigateToMatch: (String) -> Unit = {},
    onNavigateToMyTBA: () -> Unit = {},
    viewModel: EventDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
    val isFavorite by viewModel.isFavorite.collectAsStateWithLifecycle()
    val pagerState = rememberPagerState(pageCount = { TABS.size })
    val coroutineScope = rememberCoroutineScope()

    var showSignInDialog by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        viewModel.showSignInPrompt.collect { showSignInDialog = true }
    }

    if (showSignInDialog) {
        AlertDialog(
            onDismissRequest = { showSignInDialog = false },
            title = { Text("Sign in required") },
            text = { Text("Sign in to save your favorite teams and events.") },
            confirmButton = {
                TextButton(onClick = {
                    showSignInDialog = false
                    onNavigateToMyTBA()
                }) { Text("Sign In") }
            },
            dismissButton = {
                TextButton(onClick = { showSignInDialog = false }) { Text("Cancel") }
            },
        )
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = {
                Text(
                    text = uiState.event?.name ?: "Event",
                    maxLines = 1,
                )
            },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            },
            actions = {
                IconButton(onClick = viewModel::toggleFavorite) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Star else Icons.Outlined.StarBorder,
                        contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites",
                    )
                }
            },
        )

        PrimaryScrollableTabRow(
            selectedTabIndex = pagerState.currentPage,
            edgePadding = 0.dp,
        ) {
            TABS.forEachIndexed { index, title ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = { coroutineScope.launch { pagerState.animateScrollToPage(index) } },
                    text = { Text(title) },
                )
            }
        }

        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = viewModel::refreshAll,
            modifier = Modifier.fillMaxSize(),
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
            ) { page ->
                when (page) {
                    0 -> InfoTab(uiState.event)
                    1 -> TeamsTab(uiState.teams, onNavigateToTeam)
                    2 -> MatchesTab(uiState.matches, onNavigateToMatch)
                    3 -> RankingsTab(uiState.rankings)
                    4 -> AlliancesTab(uiState.alliances)
                    5 -> AwardsTab(uiState.awards)
                }
            }
        }
    }
}

@Composable
private fun InfoTab(event: Event?) {
    if (event == null) {
        LoadingBox()
        return
    }
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        item {
            Text(event.name, style = MaterialTheme.typography.headlineSmall)
        }
        val location = listOfNotNull(event.city, event.state, event.country).joinToString(", ")
        if (location.isNotEmpty()) {
            item {
                Text(
                    text = location,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
        }
        if (event.locationName != null) {
            item {
                Text(
                    text = event.locationName,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 4.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        val dateRange = formatEventDateRange(event.startDate, event.endDate)
        if (dateRange != null) {
            item {
                Text(
                    text = dateRange,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
        }
        if (event.website != null) {
            item {
                Text(
                    text = event.website,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
        }
    }
}

@Composable
private fun TeamsTab(teams: List<Team>?, onNavigateToTeam: (String) -> Unit) {
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
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToTeam(team.key) }
                    .padding(horizontal = 16.dp, vertical = 8.dp),
            ) {
                Text(
                    text = "${team.number} - ${team.nickname ?: team.name ?: ""}",
                    style = MaterialTheme.typography.bodyLarge,
                )
                val loc = listOfNotNull(team.city, team.state, team.country).joinToString(", ")
                if (loc.isNotEmpty()) {
                    Text(
                        text = loc,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
private fun MatchesTab(matches: List<Match>?, onNavigateToMatch: (String) -> Unit) {
    if (matches == null) {
        LoadingBox()
        return
    }
    if (matches.isEmpty()) {
        EmptyBox("No matches")
        return
    }
    val compLevelOrder = mapOf("qm" to 0, "ef" to 1, "qf" to 2, "sf" to 3, "f" to 4)
    val compLevelNames = mapOf("qm" to "Quals", "ef" to "Eighths", "qf" to "Quarters", "sf" to "Semis", "f" to "Finals")
    val sorted = matches.sortedWith(
        compareBy({ compLevelOrder[it.compLevel] ?: 99 }, { it.setNumber }, { it.matchNumber })
    )
    val grouped = sorted.groupBy { it.compLevel }

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        grouped.forEach { (level, levelMatches) ->
            item(key = "match_header_$level") {
                Text(
                    text = compLevelNames[level] ?: level.uppercase(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                )
            }
            items(levelMatches, key = { it.key }) { match ->
                MatchItem(match, onClick = { onNavigateToMatch(match.key) })
                HorizontalDivider()
            }
        }
    }
}

@Composable
private fun MatchItem(match: Match, onClick: () -> Unit) {
    val label = when (match.compLevel) {
        "qm" -> "Q${match.matchNumber}"
        "qf" -> "QF${match.setNumber}-${match.matchNumber}"
        "sf" -> "SF${match.setNumber}-${match.matchNumber}"
        "f" -> "F${match.setNumber}-${match.matchNumber}"
        else -> "${match.compLevel}${match.setNumber}-${match.matchNumber}"
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(0.15f),
        )
        Column(modifier = Modifier.weight(0.35f)) {
            Text(
                text = match.redTeamKeys.joinToString(", ") { it.removePrefix("frc") },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
            )
            Text(
                text = match.blueTeamKeys.joinToString(", ") { it.removePrefix("frc") },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
            )
        }
        Column(
            modifier = Modifier.weight(0.15f),
            horizontalAlignment = Alignment.End,
        ) {
            Text(
                text = match.redScore.toString(),
                style = MaterialTheme.typography.bodySmall,
                fontWeight = if (match.winningAlliance == "red") FontWeight.Bold else FontWeight.Normal,
                color = MaterialTheme.colorScheme.error,
            )
            Text(
                text = match.blueScore.toString(),
                style = MaterialTheme.typography.bodySmall,
                fontWeight = if (match.winningAlliance == "blue") FontWeight.Bold else FontWeight.Normal,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

@Composable
private fun RankingsTab(rankings: List<Ranking>?) {
    if (rankings == null) {
        LoadingBox()
        return
    }
    if (rankings.isEmpty()) {
        EmptyBox("No rankings")
        return
    }
    val sorted = rankings.sortedBy { it.rank }
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(sorted, key = { "${it.eventKey}_${it.teamKey}" }) { ranking ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "#${ranking.rank}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(0.15f),
                )
                Text(
                    text = ranking.teamKey.removePrefix("frc"),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.weight(0.25f),
                )
                Text(
                    text = "${ranking.wins}-${ranking.losses}-${ranking.ties}",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(0.25f),
                )
                Text(
                    text = "${ranking.matchesPlayed} played",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(0.35f),
                )
            }
        }
    }
}

@Composable
private fun AlliancesTab(alliances: List<Alliance>?) {
    if (alliances == null) {
        LoadingBox()
        return
    }
    if (alliances.isEmpty()) {
        EmptyBox("No alliances")
        return
    }
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(alliances, key = { "${it.eventKey}_${it.number}" }) { alliance ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
            ) {
                Text(
                    text = "Alliance ${alliance.number}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = alliance.picks.joinToString(", ") { it.removePrefix("frc") },
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 2.dp),
                )
            }
        }
    }
}

@Composable
private fun AwardsTab(awards: List<Award>?) {
    if (awards == null) {
        LoadingBox()
        return
    }
    if (awards.isEmpty()) {
        EmptyBox("No awards")
        return
    }
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(awards, key = { "${it.eventKey}_${it.awardType}_${it.teamKey}" }) { award ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
            ) {
                Text(
                    text = award.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                )
                Text(
                    text = award.teamKey.removePrefix("frc"),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun LoadingBox() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
private fun EmptyBox(message: String) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(message, style = MaterialTheme.typography.bodyLarge)
    }
}
