package com.thebluealliance.android.ui.teamevent

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.thebluealliance.android.domain.model.Alliance
import com.thebluealliance.android.domain.model.Award
import com.thebluealliance.android.domain.model.Event
import com.thebluealliance.android.domain.model.EventOPRs
import com.thebluealliance.android.domain.model.Match
import com.thebluealliance.android.domain.model.PlayoffType
import com.thebluealliance.android.domain.model.Ranking
import com.thebluealliance.android.domain.model.Team
import com.thebluealliance.android.ui.common.EmptyBox
import com.thebluealliance.android.ui.common.LoadingBox
import com.thebluealliance.android.ui.events.detail.EventDetailTab
import com.thebluealliance.android.ui.components.EventRow
import com.thebluealliance.android.ui.components.MatchItem
import com.thebluealliance.android.ui.components.MatchList
import com.thebluealliance.android.ui.components.MediaTab
import com.thebluealliance.android.ui.components.TBATabRow
import com.thebluealliance.android.ui.components.TBATopAppBar
import com.thebluealliance.android.ui.components.TeamRow
import com.thebluealliance.android.util.openUrl
import kotlinx.coroutines.launch

private val TABS = listOf("Summary", "Matches", "Media", "Stats", "Awards")

object TeamEventDetailTabs {
    const val SUMMARY = 0
    const val MATCHES = 1
    const val MEDIA = 2
    const val STATS = 3
    const val AWARDS = 4
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamEventDetailScreen(
    onNavigateUp: () -> Unit,
    onNavigateToMatch: (String) -> Unit,
    onNavigateToTeam: (String) -> Unit,
    onNavigateToEvent: (eventKey: String, initialTab: EventDetailTab) -> Unit,
    onNavigateToSearch: () -> Unit,
    viewModel: TeamEventDetailViewModel,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
    val pagerState = rememberPagerState(pageCount = { TABS.size })
    val coroutineScope = rememberCoroutineScope()

    val team = uiState.team
    val event = uiState.event

    Scaffold(
        topBar = {
            Column {
                TBATopAppBar(
                    title = {
                        if (team != null && event != null) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = "${team.number}",
                                    maxLines = 1,
                                    modifier = Modifier.clickable { onNavigateToTeam(team.key) },
                                    style = MaterialTheme.typography.titleLarge,
                                )
                                Text(
                                    text = " @ ",
                                    maxLines = 1,
                                    style = MaterialTheme.typography.titleLarge,
                                )
                                Text(
                                    text = event.shortName ?: event.name,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f).clickable { onNavigateToEvent(event.key, EventDetailTab.INFO) },
                                    style = MaterialTheme.typography.titleLarge,
                                )
                            }
                        } else {
                            Text(
                                text = "Team @ Event",
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateUp) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = onNavigateToSearch) {
                            Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
                        }
                    },
                )

                TBATabRow(selectedTabIndex = pagerState.currentPage) {
                    TABS.forEachIndexed { index, title ->
                        Tab(
                            selected = pagerState.currentPage == index,
                            onClick = { coroutineScope.launch { pagerState.animateScrollToPage(index) } },
                            text = {
                                Text(
                                    text = title,
                                    color = if (pagerState.currentPage == index) Color.White else Color.White.copy(alpha = 0.7f)
                                )
                            },
                        )
                    }
                }
            }
        },
    ) { innerPadding ->
        val bottomPadding = PaddingValues(bottom = innerPadding.calculateBottomPadding())
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding())
                .background(MaterialTheme.colorScheme.background),
        ) { page ->
            PullToRefreshBox(
                isRefreshing = isRefreshing && uiState.matches != null && uiState.awards != null,
                onRefresh = { viewModel.refreshTab(page) },
                modifier = Modifier.fillMaxSize(),
            ) {
                val evt = uiState.event
                when (page) {
                    TeamEventDetailTabs.SUMMARY -> SummaryTab(
                        teamKey = viewModel.teamKey,
                        eventKey = viewModel.eventKey,
                        event = evt,
                        team = uiState.team,
                        ranking = uiState.ranking,
                        alliances = uiState.alliances,
                        awards = uiState.awards,
                        matches = uiState.matches,
                        pitLocation = uiState.pitLocation,
                        onNavigateToEvent = onNavigateToEvent,
                        onNavigateToTeam = onNavigateToTeam,
                        onNavigateToMatch = onNavigateToMatch,
                        innerPadding = bottomPadding,
                    )
                    TeamEventDetailTabs.MATCHES -> {
                        val tm = uiState.team
                        val hasBoth = evt != null && tm != null
                        val headerCount = (if (evt != null) 1 else 0) + (if (hasBoth) 1 else 0) + (if (tm != null) 1 else 0)
                        MatchList(
                            matches = uiState.matches,
                            playoffType = evt?.playoffType ?: PlayoffType.OTHER,
                            onNavigateToMatch = onNavigateToMatch,
                            headerItemCount = headerCount,
                            headerContent = {
                                if (evt != null) {
                                    item(key = "header_event") {
                                        EventRow(
                                            event = evt,
                                            onClick = { onNavigateToEvent(evt.key, EventDetailTab.INFO) },
                                            showYear = true,
                                            showChevron = true,
                                        )
                                    }
                                }
                                if (evt != null && tm != null) {
                                    item(key = "header_divider") { HorizontalDivider() }
                                }
                                if (tm != null) {
                                    item(key = "header_team") {
                                        TeamRow(
                                            team = tm,
                                            onClick = { onNavigateToTeam(tm.key) },
                                            showChevron = true,
                                        )
                                    }
                                }
                            },
                            innerPadding = bottomPadding,
                        )
                    }
                    TeamEventDetailTabs.MEDIA -> MediaTab(
                        media = uiState.media,
                        innerPadding = bottomPadding,
                    )
                    TeamEventDetailTabs.STATS -> StatsTab(
                        teamKey = viewModel.teamKey,
                        oprs = uiState.oprs,
                        innerPadding = bottomPadding,
                    )
                    TeamEventDetailTabs.AWARDS -> {
                        val tm = uiState.team
                        AwardsTab(
                            awards = uiState.awards,
                            event = evt,
                            team = tm,
                            onNavigateToEvent = onNavigateToEvent,
                            onNavigateToTeam = onNavigateToTeam,
                            innerPadding = bottomPadding,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SummaryTab(
    teamKey: String,
    eventKey: String,
    event: Event?,
    team: Team?,
    ranking: Ranking?,
    alliances: List<Alliance>?,
    awards: List<Award>?,
    matches: List<Match>?,
    pitLocation: String?,
    onNavigateToEvent: (eventKey: String, initialTab: EventDetailTab) -> Unit,
    onNavigateToTeam: (String) -> Unit,
    onNavigateToMatch: (String) -> Unit,
    innerPadding: PaddingValues = PaddingValues.Zero,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = innerPadding,
    ) {
        // Event header
        if (event != null) {
            item(key = "header_event") {
                EventRow(
                    event = event,
                    onClick = { onNavigateToEvent(event.key, EventDetailTab.INFO) },
                    showYear = true,
                    showChevron = true,
                )
            }
        }
        if (event != null && team != null) {
            item(key = "header_divider") { HorizontalDivider() }
        }
        if (team != null) {
            item(key = "header_team") {
                TeamRow(
                    team = team,
                    onClick = { onNavigateToTeam(team.key) },
                    showChevron = true,
                )
            }
        }

        // Info header (ranking, alliance, pit location)
        val teamAlliance = alliances?.firstOrNull { alliance ->
            teamKey in alliance.picks || teamKey == alliance.backupIn
        }
        if (ranking != null || teamAlliance != null || pitLocation != null) {
            item(key = "summary_info_header") {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF5C6BC0))
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                ) {
                    Text(
                        text = "Info",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                    )
                }
            }
        }

        // Ranking
        var infoItemCount = 0
        if (ranking != null) {
            item(key = "summary_ranking") {
                InfoRow(
                    label = "Rank ${ranking.rank}",
                    value = "${ranking.wins}-${ranking.losses}-${ranking.ties}",
                    onClick = { event?.let { onNavigateToEvent(it.key, EventDetailTab.RANKINGS) } },
                )
            }
            infoItemCount++
        }

        // Alliance
        if (teamAlliance != null) {
            if (infoItemCount > 0) {
                item(key = "summary_alliance_divider") { HorizontalDivider() }
            }
            item(key = "summary_alliance") {
                val role = when {
                    teamKey == teamAlliance.backupIn -> "Backup"
                    teamAlliance.picks.indexOf(teamKey) == 0 -> "Captain"
                    else -> "Pick ${teamAlliance.picks.indexOf(teamKey)}"
                }
                InfoRow(
                    label = "Alliance ${teamAlliance.number}",
                    value = role,
                    onClick = { event?.let { onNavigateToEvent(it.key, EventDetailTab.ALLIANCES) } },
                )
            }
            infoItemCount++
        }

        // Pit Location
        if (pitLocation != null) {
            if (infoItemCount > 0) {
                item(key = "summary_pit_location_divider") { HorizontalDivider() }
            }
            item(key = "summary_pit_location") {
                val context = LocalContext.current
                val teamNumber = teamKey.removePrefix("frc")
                InfoRow(
                    label = "Pit Location",
                    labelSuffix = "(via FRC Nexus)",
                    value = pitLocation,
                    onClick = {
                        context.openUrl("https://frc.nexus/en/event/$eventKey/team/$teamNumber/map")
                    },
                )
            }
        }

        // Awards
        val teamAwards = awards
        if (teamAwards != null && teamAwards.isNotEmpty()) {
            item(key = "summary_awards") {
                SummarySection(label = "Awards") {
                    teamAwards.forEach { award ->
                        Text(
                            text = award.name,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }
            }
        }

        // Last Match / Next Match
        if (matches != null) {
            val sortedMatches = matches.sortedWith(
                compareBy({ it.compLevel.order }, { it.setNumber }, { it.matchNumber })
            )
            val lastPlayed = sortedMatches.lastOrNull { it.redScore >= 0 }
            val nextUnplayed = sortedMatches.firstOrNull { it.redScore < 0 }
            val playoffType = event?.playoffType ?: PlayoffType.OTHER

            if (lastPlayed != null || nextUnplayed != null) {
                item(key = "summary_match_header") {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF5C6BC0))
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                    ) {
                        Text(
                            text = "Recent Matches",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                        )
                    }
                }
            }

            if (lastPlayed != null) {
                item(key = "summary_last_match") {
                    SummarySection(label = "Last Match") {
                        MatchItem(
                            match = lastPlayed,
                            playoffType = playoffType,
                            onClick = { onNavigateToMatch(lastPlayed.key) },
                        )
                    }
                }
            }

            if (nextUnplayed != null) {
                item(key = "summary_next_match") {
                    SummarySection(label = "Next Match") {
                        MatchItem(
                            match = nextUnplayed,
                            playoffType = playoffType,
                            onClick = { onNavigateToMatch(nextUnplayed.key) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SummarySection(
    label: String,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 4.dp),
        )
        content()
    }
}

@Composable
private fun InfoRow(
    label: String,
    labelSuffix: String? = null,
    value: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
            if (labelSuffix != null) {
                Text(
                    text = " $labelSuffix",
                    style = MaterialTheme.typography.titleMedium,
                )
            }
        }
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun StatsTab(
    teamKey: String,
    oprs: EventOPRs?,
    innerPadding: PaddingValues = PaddingValues.Zero,
) {
    if (oprs == null) {
        LoadingBox(modifier = Modifier.padding(innerPadding))
        return
    }
    val opr = oprs.oprs[teamKey]
    val dpr = oprs.dprs[teamKey]
    val ccwm = oprs.ccwms[teamKey]
    if (opr == null && dpr == null && ccwm == null) {
        EmptyBox(
            modifier = Modifier.padding(innerPadding),
            message = "No stats available"
        )
        return
    }
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        if (opr != null) StatRow("OPR", opr)
        if (dpr != null) StatRow("DPR", dpr)
        if (ccwm != null) StatRow("CCWM", ccwm)
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = "Learn more about OPR",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.clickable { context.openUrl("https://www.thebluealliance.com/opr") },
        )
    }
}

@Composable
private fun StatRow(label: String, value: Double) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
        )
        Text(
            text = "%.2f".format(value),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun AwardsTab(
    awards: List<Award>?,
    event: Event?,
    team: Team?,
    onNavigateToEvent: (eventKey: String, initialTab: EventDetailTab) -> Unit,
    onNavigateToTeam: (String) -> Unit,
    innerPadding: PaddingValues = PaddingValues.Zero,
) {
    if (awards == null) {
        LoadingBox(
            modifier = Modifier.padding(innerPadding)
        )
        return
    }
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = innerPadding,
    ) {
        if (event != null) {
            item(key = "header_event") {
                EventRow(
                    event = event,
                    onClick = { onNavigateToEvent(event.key, EventDetailTab.INFO) },
                    showYear = true,
                    showChevron = true,
                )
            }
        }
        if (event != null && team != null) {
            item(key = "header_divider") { HorizontalDivider() }
        }
        if (team != null) {
            item(key = "header_team") {
                TeamRow(
                    team = team,
                    onClick = { onNavigateToTeam(team.key) },
                    showChevron = true,
                )
            }
        }
        if (awards.isEmpty()) {
            item {
                Box(
                    Modifier.fillMaxWidth().padding(32.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("No awards", style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
        items(awards, key = { "${it.awardType}_${it.awardee.orEmpty()}" }) { award ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
            ) {
                Text(
                    text = award.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                )
                if (award.awardee != null) {
                    Text(
                        text = award.awardee,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}
