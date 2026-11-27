package com.thebluealliance.android.ui.more

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.thebluealliance.android.data.remote.dto.GitHubContributorDto
import com.thebluealliance.android.ui.common.ErrorBox
import com.thebluealliance.android.ui.common.LoadingBox
import com.thebluealliance.android.ui.components.ReadingWidthBox
import com.thebluealliance.android.ui.components.TBATopAppBar
import com.thebluealliance.android.ui.components.TooltipIconButton
import com.thebluealliance.android.ui.components.TopBarAction
import com.thebluealliance.android.ui.components.TopBarTitle
import com.thebluealliance.android.ui.components.TopLevelContentWindowInsets
import com.thebluealliance.android.ui.components.handCursor
import com.thebluealliance.android.ui.components.onlyBottom
import com.thebluealliance.android.ui.components.pageBackground
import com.thebluealliance.android.ui.components.withoutBottom
import com.thebluealliance.android.ui.theme.TbaIcons
import com.thebluealliance.android.util.openUrl

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun ThanksScreen(
    onNavigateUp: () -> Unit,
    onNavigateToSearch: () -> Unit,
    viewModel: ThanksViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        contentWindowInsets = TopLevelContentWindowInsets,
        topBar = {
            TBATopAppBar(
                title = { TopBarTitle("Thanks") },
                navigationIcon = {
                    TooltipIconButton("Back", TbaIcons.ArrowBack, onNavigateUp)
                },
                secondaryActions =
                    listOf(
                        TopBarAction("Search", TbaIcons.Search, onNavigateToSearch),
                    ),
            )
        },
    ) { innerPadding ->
        when (val state = uiState) {
            is ThanksUiState.Loading ->
                LoadingBox(
                    modifier = Modifier.padding(innerPadding.withoutBottom()),
                )
            is ThanksUiState.Error ->
                ErrorBox(
                    modifier = Modifier.padding(innerPadding.withoutBottom()),
                    message = state.message,
                    onRetry = viewModel::retry,
                )
            is ThanksUiState.Success -> {
                ReadingWidthBox(
                    innerPadding.onlyBottom(),
                    Modifier
                        .padding(innerPadding.withoutBottom())
                        .fillMaxSize(),
                ) { readingPadding ->
                    LazyColumn(
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .pageBackground(),
                        contentPadding = readingPadding,
                    ) {
                        items(state.contributors) { contributor ->
                            ContributorRow(contributor)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ContributorRow(contributor: GitHubContributorDto) {
    val context = LocalContext.current
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .handCursor()
                .clickable(onClickLabel = "Open in browser") {
                    context.openUrl(contributor.htmlUrl)
                }.padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AsyncImage(
            model = contributor.avatarUrl,
            // Decorative: the login is read from the text beside it.
            contentDescription = null,
            modifier = Modifier.size(40.dp).clip(CircleShape),
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = contributor.login,
                style = MaterialTheme.typography.bodyLarge,
            )
            Text(
                text = contributionsLabel(contributor.contributions),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

internal fun contributionsLabel(count: Int): String =
    "$count ${if (count == 1) "contribution" else "contributions"}"
