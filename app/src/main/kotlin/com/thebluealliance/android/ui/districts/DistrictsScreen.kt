package com.thebluealliance.android.ui.districts

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
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
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalInputModeManager
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.thebluealliance.android.domain.model.District
import com.thebluealliance.android.ui.common.EmptyBox
import com.thebluealliance.android.ui.common.StateContent
import com.thebluealliance.android.ui.common.UiState
import com.thebluealliance.android.ui.components.FocusHandoffEffect
import com.thebluealliance.android.ui.components.ReadingWidthBox
import com.thebluealliance.android.ui.components.TBATopAppBar
import com.thebluealliance.android.ui.components.TopBarAction
import com.thebluealliance.android.ui.components.TopBarYearPicker
import com.thebluealliance.android.ui.components.TopLevelContentWindowInsets
import com.thebluealliance.android.ui.components.handCursor
import com.thebluealliance.android.ui.components.onlyBottom
import com.thebluealliance.android.ui.components.pageBackground
import com.thebluealliance.android.ui.components.rememberFocusHandoff
import com.thebluealliance.android.ui.components.returnsFocus
import com.thebluealliance.android.ui.components.withoutBottom
import com.thebluealliance.android.ui.theme.TbaIcons
import kotlinx.coroutines.flow.Flow

private const val FIRST_DISTRICT_YEAR = 2009

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

    // Pressing "Show <year> districts" by keyboard removes the button; focus moves to that
    // year's first district, or back to the button when that year is empty too.
    val inputModeManager = LocalInputModeManager.current
    val seasonFocus = rememberFocusHandoff()
    FocusHandoffEffect(
        seasonFocus,
        current = selectedYear,
        hasContent = uiState is UiState.Success,
        controlShown = uiState is UiState.Empty,
    )

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
        contentWindowInsets = TopLevelContentWindowInsets,
        topBar = {
            TBATopAppBar(
                onRefresh = viewModel::refreshDistricts,
                isRefreshing = isRefreshing && uiState !is UiState.Loading,
                title = {
                    TopBarYearPicker(
                        selectedYear = selectedYear,
                        years =
                            if (selectedYear >
                                0
                            ) {
                                (maxYear downTo FIRST_DISTRICT_YEAR).toList()
                            } else {
                                emptyList()
                            },
                        onYearSelected = viewModel::selectYear,
                        title = { Text("Districts") },
                    )
                },
                secondaryActions =
                    listOf(
                        TopBarAction("Search", TbaIcons.Search, onNavigateToSearch),
                    ),
                showLamp = true,
            )
        },
    ) { innerPadding ->
        StateContent(
            state = uiState,
            isRefreshing = isRefreshing,
            onRefresh = viewModel::refreshDistricts,
            modifier =
                Modifier
                    .pageBackground()
                    .padding(innerPadding.withoutBottom())
                    .fillMaxSize(),
            empty = {
                EmptyBox(
                    message = "No districts found for $selectedYear",
                    action =
                        if (selectedYear > FIRST_DISTRICT_YEAR) {
                            {
                                FilledTonalButton(
                                    onClick = {
                                        seasonFocus.arm(
                                            selectedYear - 1,
                                            inputModeManager.inputMode,
                                        )
                                        viewModel.selectYear(selectedYear - 1)
                                    },
                                    modifier =
                                        Modifier
                                            .focusRequester(seasonFocus.requester)
                                            .handCursor()
                                            .heightIn(min = 48.dp),
                                ) {
                                    Text("Show ${selectedYear - 1} districts")
                                }
                            }
                        } else {
                            null
                        },
                )
            },
        ) { districts ->
            ReadingWidthBox(innerPadding.onlyBottom(), Modifier.fillMaxSize()) { readingPadding ->
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = readingPadding,
                ) {
                    itemsIndexed(districts, key = {
                        _,
                        district,
                        ->
                        district.key
                    }) { index, district ->
                        DistrictItem(
                            district = district,
                            onClick = { onNavigateToDistrict(district.key) },
                            modifier =
                                if (index == 0) {
                                    Modifier.focusRequester(seasonFocus.requester)
                                } else {
                                    Modifier
                                },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DistrictItem(
    district: District,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Text(
        text = district.displayName,
        style = MaterialTheme.typography.titleMedium,
        modifier =
            modifier
                .fillMaxWidth()
                .returnsFocus()
                .handCursor()
                .clickable(onClickLabel = "View district", onClick = onClick)
                .padding(horizontal = 16.dp, vertical = 12.dp),
    )
}
