package com.thebluealliance.android.ui.more

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.mikepenz.aboutlibraries.ui.compose.android.produceLibraries
import com.mikepenz.aboutlibraries.ui.compose.m3.LibrariesContainer
import com.thebluealliance.android.BuildConfig
import com.thebluealliance.android.ui.components.SectionHeader
import com.thebluealliance.android.ui.components.TBATopAppBar

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun AboutScreen(
    onNavigateUp: () -> Unit,
    onNavigateToSearch: () -> Unit,
) {
    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal),
        topBar = {
            TBATopAppBar(
                title = { Text("About") },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToSearch) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                },
            )
        },
    ) { innerPadding ->
        val libs by produceLibraries()
        LibrariesContainer(
            libraries = libs,
            modifier =
                Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
            header = { buildInfoSection() },
        )
    }
}

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
