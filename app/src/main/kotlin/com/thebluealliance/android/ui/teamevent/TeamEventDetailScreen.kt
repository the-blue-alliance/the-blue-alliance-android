package com.thebluealliance.android.ui.teamevent

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.thebluealliance.android.domain.model.Award
import com.thebluealliance.android.domain.model.PlayoffType
import com.thebluealliance.android.ui.common.EmptyBox
import com.thebluealliance.android.ui.common.LoadingBox
import com.thebluealliance.android.ui.components.EventRow
import com.thebluealliance.android.ui.components.MatchList
import com.thebluealliance.android.ui.components.TBATabRow
import com.thebluealliance.android.ui.components.TBATopAppBar
import com.thebluealliance.android.ui.components.TeamRow
import kotlinx.coroutines.launch

private val TABS = listOf("Matches", "Awards")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamEventDetailScreen(
    onNavigateUp: () -> Unit,
    onNavigateToMatch: (String) -> Unit,
    onNavigateToTeam: (String) -> Unit,
    onNavigateToEvent: (String) -> Unit,
    onNavigateToSearch: () -> Unit,
    viewModel: TeamEventDetailViewModel,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
    val pagerState = rememberPagerState(pageCount = { TABS.size })
    val coroutineScope = rememberCoroutineScope()

    val team = uiState.team
    val event = uiState.event
    val titleText = if (team != null && event != null) {
        "${team.number} @ ${event.shortName ?: event.name}"
    } else {
        "Team @ Event"
    }

    Scaffold(
        topBar = {
            Column {
                TBATopAppBar(
                    title = {
                        Text(
                            text = titleText,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
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
        PullToRefreshBox(
            isRefreshing = isRefreshing && uiState.matches != null && uiState.awards != null,
            onRefresh = viewModel::refreshAll,
            modifier = Modifier.fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
            ) { page ->
                val evt = uiState.event
                when (page) {
                    0 -> {
                        val tm = uiState.team
                        val headerCount = (if (evt != null) 1 else 0) + (if (tm != null) 1 else 0)
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
                                            onClick = { onNavigateToEvent(evt.key) },
                                            showYear = true,
                                        )
                                    }
                                }
                                if (tm != null) {
                                    item(key = "header_team") {
                                        TeamRow(
                                            team = tm,
                                            onClick = { onNavigateToTeam(tm.key) },
                                        )
                                    }
                                }
                            },
                            innerPadding = innerPadding,
                        )
                    }
                    1 -> AwardsTab(
                        awards = uiState.awards,
                        innerPadding = innerPadding,
                    )
                }
            }
        }
    }
}

@Composable
private fun AwardsTab(
    awards: List<Award>?,
    innerPadding: PaddingValues = PaddingValues.Zero,
) {
    if (awards == null) {
        LoadingBox(
            modifier = Modifier.padding(innerPadding)
        )
        return
    }
    if (awards.isEmpty()) {
        EmptyBox(
            modifier = Modifier.padding(innerPadding),
            message = "No awards"
        )
        return
    }
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = innerPadding,
    ) {
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
