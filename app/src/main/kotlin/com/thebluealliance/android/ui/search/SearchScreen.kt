package com.thebluealliance.android.ui.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import com.thebluealliance.android.ui.components.TBATopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.thebluealliance.android.ui.components.EventRow
import com.thebluealliance.android.ui.components.SectionHeader
import com.thebluealliance.android.ui.components.SectionHeaderInfo
import com.thebluealliance.android.ui.components.TeamRow
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onNavigateUp: () -> Unit,
    onNavigateToTeam: (String) -> Unit,
    onNavigateToEvent: (String) -> Unit,
    viewModel: SearchViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val focusRequester = remember { FocusRequester() }
    val lazyListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    LaunchedEffect(uiState.teams, uiState.events) {
        lazyListState.scrollToItem(0)
    }

    val headerInfos = remember(uiState.teams.size, uiState.events.size) {
        buildList {
            var index = 0
            if (uiState.teams.isNotEmpty()) {
                add(SectionHeaderInfo("teams_header", "Teams", index))
                index += 1 + uiState.teams.size
            }
            if (uiState.events.isNotEmpty()) {
                add(SectionHeaderInfo("events_header", "Events", index))
            }
        }
    }

    val headerKeys = remember(headerInfos) {
        headerInfos.map { it.key }.toSet()
    }

    val stuckHeaderKey by remember(headerKeys) {
        derivedStateOf {
            val stuck = lazyListState.layoutInfo.visibleItemsInfo
                .firstOrNull { item ->
                    val key = item.key as? String
                    key != null && key in headerKeys && item.offset <= 0
                }?.key as? String
            stuck ?: headerInfos.firstOrNull()?.key
        }
    }

    Scaffold(
        topBar = {
            TBATopAppBar(
                title = {
                    TextField(
                        value = uiState.query,
                        onValueChange = viewModel::onQueryChanged,
                        placeholder = { Text("Search teams & events") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                            focusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            cursorColor = MaterialTheme.colorScheme.primary,
                            focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                            unfocusedIndicatorColor = MaterialTheme.colorScheme.outline,
                            focusedTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unfocusedTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        ),
                        trailingIcon = {
                            if (uiState.query.isNotEmpty()) {
                                IconButton(onClick = { viewModel.onQueryChanged("") }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear")
                                }
                            }
                        },
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                state = lazyListState,
            ) {
                if (uiState.teams.isNotEmpty()) {
                    stickyHeader(key = "teams_header") {
                        SectionHeader(
                            label = "Teams",
                            isStuck = stuckHeaderKey == "teams_header",
                            allHeaders = headerInfos,
                            onHeaderSelected = { info ->
                                coroutineScope.launch {
                                    lazyListState.animateScrollToItem(info.itemIndex)
                                }
                            },
                        )
                    }
                    items(uiState.teams, key = { "team_${it.key}" }) { team ->
                        TeamRow(team = team, onClick = { onNavigateToTeam(team.key) })
                    }
                }
                if (uiState.events.isNotEmpty()) {
                    stickyHeader(key = "events_header") {
                        SectionHeader(
                            label = "Events",
                            isStuck = stuckHeaderKey == "events_header",
                            allHeaders = headerInfos,
                            onHeaderSelected = { info ->
                                coroutineScope.launch {
                                    lazyListState.animateScrollToItem(info.itemIndex)
                                }
                            },
                        )
                    }
                    items(uiState.events, key = { "event_${it.key}" }) { event ->
                        EventRow(event = event, onClick = { onNavigateToEvent(event.key) }, showYear = true)
                    }
                }
            }
        }
    }
}
