package com.thebluealliance.android.ui.districts

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.thebluealliance.android.domain.model.District
import com.thebluealliance.android.ui.common.EmptyBox
import com.thebluealliance.android.ui.common.StateContent
import com.thebluealliance.android.ui.common.UiState
import com.thebluealliance.android.ui.components.TBATopAppBar
import com.thebluealliance.android.ui.components.TopBarYearPicker
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DistrictsScreen(
    onNavigateToDistrict: (String) -> Unit = {},
    onNavigateToSearch: () -> Unit,
    reselectFlow: Flow<Unit>,
    viewModel: DistrictsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val selectedYear by viewModel.selectedYear.collectAsStateWithLifecycle()
    val maxYear by viewModel.maxYear.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()

    // Reset scroll position when year changes (but not on initial composition/back navigation)
    var previousYear by rememberSaveable { mutableIntStateOf(selectedYear) }
    LaunchedEffect(selectedYear) {
        if (selectedYear != previousYear) {
            previousYear = selectedYear
            listState.scrollToItem(0)
        }
    }

    LaunchedEffect(reselectFlow) {
        reselectFlow.collect {
            listState.animateScrollToItem(0)
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal),
        topBar = {
            TBATopAppBar(
                title = {
                    TopBarYearPicker(
                        selectedYear = selectedYear,
                        years =
                            if (selectedYear >
                                0
                            ) {
                                (maxYear downTo 2009).toList()
                            } else {
                                emptyList()
                            },
                        onYearSelected = viewModel::selectYear,
                        title = { Text("Districts") },
                    )
                },
                actions = {
                    IconButton(onClick = onNavigateToSearch) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                },
                showLamp = true,
            )
        },
    ) { innerPadding ->
        StateContent(
            state = uiState,
            isRefreshing = isRefreshing && uiState !is UiState.Loading,
            onRefresh = viewModel::refreshDistricts,
            modifier =
                Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
            empty = { EmptyBox("No districts found for $selectedYear") },
        ) { districts ->
            LazyColumn(state = listState, modifier = Modifier.fillMaxSize()) {
                items(districts, key = { it.key }) { district ->
                    DistrictItem(
                        district = district,
                        onClick = { onNavigateToDistrict(district.key) },
                    )
                }
            }
        }
    }
}

@Composable
private fun DistrictItem(
    district: District,
    onClick: () -> Unit,
) {
    Text(
        text = district.displayName,
        style = MaterialTheme.typography.titleMedium,
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(horizontal = 16.dp, vertical = 12.dp),
    )
}
