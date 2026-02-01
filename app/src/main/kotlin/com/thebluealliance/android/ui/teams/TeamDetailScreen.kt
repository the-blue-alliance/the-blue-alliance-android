package com.thebluealliance.android.ui.teams

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.thebluealliance.android.domain.model.Event
import com.thebluealliance.android.domain.model.Media
import com.thebluealliance.android.domain.model.Team
import kotlinx.coroutines.launch

private val TABS = listOf("Info", "Events", "Media")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamDetailScreen(
    onNavigateBack: () -> Unit,
    onNavigateToEvent: (String) -> Unit,
    viewModel: TeamDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
    val isFavorite by viewModel.isFavorite.collectAsStateWithLifecycle()
    val pagerState = rememberPagerState(pageCount = { TABS.size })
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = {
                val team = uiState.team
                Text(
                    text = if (team != null) "${team.number} - ${team.nickname ?: ""}" else "Team",
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
                    0 -> InfoTab(uiState.team)
                    1 -> EventsTab(uiState.events, onNavigateToEvent)
                    2 -> MediaTab(uiState.media)
                }
            }
        }
    }
}

@Composable
private fun InfoTab(team: Team?) {
    if (team == null) {
        LoadingBox()
        return
    }
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        item {
            Text(
                text = "Team ${team.number}",
                style = MaterialTheme.typography.headlineSmall,
            )
        }
        if (team.nickname != null) {
            item {
                Text(
                    text = team.nickname,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
        }
        if (team.name != null) {
            item {
                Text(
                    text = team.name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
        }
        val location = listOfNotNull(team.city, team.state, team.country).joinToString(", ")
        if (location.isNotEmpty()) {
            item {
                Text(
                    text = location,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
        }
        if (team.rookieYear != null) {
            item {
                Text(
                    text = "Rookie Year: ${team.rookieYear}",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
        }
    }
}

@Composable
private fun EventsTab(events: List<Event>?, onNavigateToEvent: (String) -> Unit) {
    if (events == null) {
        LoadingBox()
        return
    }
    if (events.isEmpty()) {
        EmptyBox("No events")
        return
    }
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(events, key = { it.key }) { event ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToEvent(event.key) }
                    .padding(horizontal = 16.dp, vertical = 12.dp),
            ) {
                Text(
                    text = event.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                )
                val location = listOfNotNull(event.city, event.state, event.country)
                    .joinToString(", ")
                if (location.isNotEmpty()) {
                    Text(
                        text = location,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                val dateRange = listOfNotNull(event.startDate, event.endDate).joinToString(" - ")
                if (dateRange.isNotEmpty()) {
                    Text(
                        text = dateRange,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
private fun MediaTab(media: List<Media>?) {
    if (media == null) {
        LoadingBox()
        return
    }
    if (media.isEmpty()) {
        EmptyBox("No media")
        return
    }
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(media, key = { "${it.type}_${it.foreignKey}" }) { item ->
            val url = mediaUrl(item)
            if (url != null) {
                AsyncImage(
                    model = url,
                    contentDescription = "Team media",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .clip(MaterialTheme.shapes.medium),
                )
            }
        }
    }
}

private fun mediaUrl(media: Media): String? = when (media.type) {
    "imgur" -> "https://i.imgur.com/${media.foreignKey}.png"
    "cdphotothread" -> "https://www.chiefdelphi.com/media/img/${media.foreignKey}"
    "instagram-image" -> "https://www.instagram.com/p/${media.foreignKey}/media/"
    "youtube" -> "https://img.youtube.com/vi/${media.foreignKey}/hqdefault.jpg"
    "grabcad" -> null // no direct image URL
    "onshape" -> null
    else -> null
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
