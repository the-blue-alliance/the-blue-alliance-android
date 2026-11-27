package com.thebluealliance.android.ui.more

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
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
import com.thebluealliance.android.ui.components.arrowCursor
import com.thebluealliance.android.ui.components.handCursor
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
            LibrariesContainer(
                libraries = libs,
                modifier =
                    Modifier
                        .fillMaxSize()
                        .pageBackground()
                        // Every library row is clickable, and the rows take no modifier, so the
                        // list shows the hand and the build info above it opts back out.
                        .handCursor(),
                contentPadding = readingPadding,
                header = { buildInfoSection() },
            )
        }
    }
}

private fun LazyListScope.buildInfoSection() {
    item(key = "build-header") { SectionHeader("Build", Modifier.arrowCursor()) }
    item(key = "build-version") { BuildInfoRow("Version", BuildConfig.VERSION_NAME) }
    item(key = "build-commit") { BuildInfoRow("Commit", BuildConfig.GIT_HASH) }
    item(key = "build-time") { BuildInfoRow("Built", buildTimeLabel()) }
    item(key = "libraries-header") {
        SectionHeader("Open source libraries", Modifier.arrowCursor())
    }
}

@Composable
private fun BuildInfoRow(
    label: String,
    value: String,
) {
    ListItem(
        headlineContent = { Text(label) },
        modifier = Modifier.arrowCursor(),
        supportingContent = { Text(value) },
    )
}
