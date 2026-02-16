package com.thebluealliance.android.ui.teamevent

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.thebluealliance.android.domain.model.Award
import com.thebluealliance.android.ui.components.EventRow
import com.thebluealliance.android.ui.components.MatchList
import com.thebluealliance.android.ui.components.TeamRow
import kotlinx.coroutines.launch

private val TABS = listOf("Matches", "Awards")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamEventDetailScreen(
    onNavigateBack: () -> Unit,
    onNavigateToMatch: (String) -> Unit,
    onNavigateToTeam: (String) -> Unit,
    onNavigateToEvent: (String) -> Unit,
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

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            windowInsets = WindowInsets(0),
            title = { Text(text = titleText, maxLines = 1, overflow = TextOverflow.Ellipsis) },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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
            isRefreshing = isRefreshing && uiState.matches != null && uiState.awards != null,
            onRefresh = viewModel::refreshAll,
            modifier = Modifier.fillMaxSize(),
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
            ) { page ->
                when (page) {
                    0 -> MatchList(
                        matches = uiState.matches,
                        onNavigateToMatch = onNavigateToMatch,
                        headerContent = {
                            val evt = uiState.event
                            if (evt != null) {
                                item(key = "header_event") {
                                    EventRow(
                                        event = evt,
                                        onClick = { onNavigateToEvent(evt.key) },
                                        showYear = true,
                                    )
                                }
                            }
                            val tm = uiState.team
                            if (tm != null) {
                                item(key = "header_team") {
                                    TeamRow(
                                        team = tm,
                                        onClick = { onNavigateToTeam(tm.key) },
                                    )
                                }
                            }
                        },
                    )
                    1 -> AwardsTab(uiState.awards)
                }
            }
        }
    }
}

@Composable
private fun AwardsTab(awards: List<Award>?) {
    if (awards == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }
    if (awards.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No awards", style = MaterialTheme.typography.bodyLarge)
        }
        return
    }
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(awards, key = { "${it.awardType}_${it.awardee.orEmpty()}" }) { award ->
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
