package com.thebluealliance.android.ui.more

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mikepenz.aboutlibraries.ui.compose.m3.LibrariesContainer
import androidx.navigation3.runtime.NavKey
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import com.thebluealliance.android.ui.components.TBABottomBar

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun AboutScreen(
    onNavigateUp: () -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToTopLevel: (NavKey) -> Unit,
    currentRoute: NavKey,
) {
    Scaffold(
        topBar = {
            TopAppBar(
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
        bottomBar = {
            TBABottomBar(
                currentRoute = currentRoute,
                onNavigate = onNavigateToTopLevel,
                onReselect = { /* no-op */ },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            LibrariesContainer(modifier = Modifier.fillMaxSize())
        }
    }
}
