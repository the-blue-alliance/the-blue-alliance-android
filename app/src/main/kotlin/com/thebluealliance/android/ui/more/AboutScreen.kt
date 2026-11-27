package com.thebluealliance.android.ui.more

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.mikepenz.aboutlibraries.ui.compose.android.produceLibraries
import com.mikepenz.aboutlibraries.ui.compose.m3.LibrariesContainer
import com.thebluealliance.android.BuildConfig
import com.thebluealliance.android.ui.components.ReadingWidthBox
import com.thebluealliance.android.ui.components.SectionHeader
import com.thebluealliance.android.ui.components.TBATopAppBar
import com.thebluealliance.android.ui.components.TopBarAction
import com.thebluealliance.android.ui.components.TopBarTitle
import com.thebluealliance.android.ui.components.TopLevelContentWindowInsets
import com.thebluealliance.android.ui.components.handCursor
import com.thebluealliance.android.ui.components.handOverListItems
import com.thebluealliance.android.ui.components.onlyBottom
import com.thebluealliance.android.ui.components.pageBackground
import com.thebluealliance.android.ui.components.withoutBottom
import com.thebluealliance.android.ui.theme.TbaIcons

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun AboutScreen(
    onNavigateUp: () -> Unit,
    onNavigateToSearch: () -> Unit,
) {
    Scaffold(
        contentWindowInsets = TopLevelContentWindowInsets,
        topBar = {
            TBATopAppBar(
                title = { TopBarTitle("About") },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp, modifier = Modifier.handCursor()) {
                        Icon(TbaIcons.ArrowBack, contentDescription = "Back")
                    }
                },
                secondaryActions =
                    listOf(
                        TopBarAction("Search", TbaIcons.Search, onNavigateToSearch),
                    ),
            )
        },
    ) { innerPadding ->
        val libs by produceLibraries()
        ReadingWidthBox(
            innerPadding.onlyBottom(),
            Modifier
                .padding(innerPadding.withoutBottom())
                .fillMaxSize(),
        ) { readingPadding ->
            val listState = rememberLazyListState()
            LibrariesContainer(
                libraries = libs,
                lazyListState = listState,
                modifier =
                    Modifier
                        .fillMaxSize()
                        .pageBackground()
                        // Each library row is clickable, but the rows take no modifier of their own.
                        .handOverListItems(listState, readingPadding) { it !in BuildInfoKeys },
                contentPadding = readingPadding,
                header = { buildInfoSection() },
            )
        }
    }
}

/** Keys of the build info items above the library rows, which aren't clickable. */
private val BuildInfoKeys =
    setOf("build-header", "build-version", "build-commit", "build-time", "libraries-header")

private fun LazyListScope.buildInfoSection() {
    item(key = "build-header") { SectionHeader("Build") }
    item(key = "build-version") { BuildInfoRow("Version", BuildConfig.VERSION_NAME) }
    item(key = "build-commit") { BuildInfoRow("Commit", BuildConfig.GIT_HASH) }
    item(key = "build-time") { BuildInfoRow("Built", buildTimeLabel()) }
    item(key = "libraries-header") { SectionHeader("Open source libraries") }
}

@Composable
private fun BuildInfoRow(
    label: String,
    value: String,
) {
    ListItem(
        headlineContent = { Text(label) },
        supportingContent = { Text(value) },
    )
}
