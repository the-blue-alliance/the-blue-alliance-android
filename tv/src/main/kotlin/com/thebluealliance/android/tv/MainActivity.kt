package com.thebluealliance.android.tv

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.thebluealliance.android.tv.ui.about.AboutScreen
import com.thebluealliance.android.tv.ui.events.EventsScreen
import com.thebluealliance.android.tv.ui.events.EventsViewModel
import com.thebluealliance.android.tv.ui.theme.TbaTvTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TbaTvTheme {
                // One-screen swap instead of a nav library: the app has exactly two
                // destinations. Back from About returns to the feed (handled in AboutScreen);
                // Back from the feed falls through to the launcher (TV-DB).
                var showAbout by rememberSaveable { mutableStateOf(false) }
                if (showAbout) {
                    AboutScreen(onBack = { showAbout = false })
                } else {
                    val viewModel: EventsViewModel = viewModel(factory = EventsViewModel.Factory)
                    EventsScreen(viewModel, onAboutClick = { showAbout = true })
                }
            }
        }
    }
}
