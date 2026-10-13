package com.thebluealliance.android.tv

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.thebluealliance.android.tv.ui.about.AboutScreen
import com.thebluealliance.android.tv.ui.events.EventsScreen
import com.thebluealliance.android.tv.ui.events.EventsViewModel
import com.thebluealliance.android.tv.ui.events.FeedFocusState
import com.thebluealliance.android.tv.ui.theme.TbaTvTheme

private const val SCREEN_FADE_MILLIS = 220
private const val FEED_STATE_KEY = "feed"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TbaTvTheme {
                // One-screen swap instead of a nav library: the app has exactly two
                // destinations. Back from About returns to the feed (handled in AboutScreen);
                // Back from the feed falls through to the launcher (TV-DB).
                var showAbout by rememberSaveable { mutableStateOf(false) }
                // Activity-scoped, so the feed keeps its data (no skeleton flash) while About shows.
                val viewModel: EventsViewModel = viewModel(factory = EventsViewModel.Factory)
                // The feed's composition is disposed while About shows. The holder keeps its
                // saveable state (column and per-row scroll positions) for the return trip, and the
                // focus guards live out here so Back lands on ⓘ instead of re-focusing card one.
                val feedStateHolder = rememberSaveableStateHolder()
                val feedFocusState = remember { FeedFocusState() }
                // Crossfade composes both screens mid-fade; focus is safe because each side
                // requests focus from onPlaced, i.e. only once its target is laid out.
                Crossfade(
                    targetState = showAbout,
                    animationSpec = tween(SCREEN_FADE_MILLIS),
                    label = "screen",
                ) { about ->
                    if (about) {
                        AboutScreen(
                            onBack = {
                                // About's BackHandler stays live while it fades out; a second
                                // quick Back must not re-arm the one-shot ⓘ focus request.
                                if (showAbout) {
                                    feedFocusState.onAboutClosed()
                                    showAbout = false
                                }
                            },
                        )
                    } else {
                        feedStateHolder.SaveableStateProvider(FEED_STATE_KEY) {
                            EventsScreen(
                                viewModel = viewModel,
                                focusState = feedFocusState,
                                onAboutClick = { showAbout = true },
                            )
                        }
                    }
                }
            }
        }
    }
}
