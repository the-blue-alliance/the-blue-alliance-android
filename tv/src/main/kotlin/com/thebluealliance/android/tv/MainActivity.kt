package com.thebluealliance.android.tv

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.thebluealliance.android.tv.ui.about.AboutScreen
import com.thebluealliance.android.tv.ui.events.EventsScreen
import com.thebluealliance.android.tv.ui.events.EventsViewModel
import com.thebluealliance.android.tv.ui.events.FeedFocusState
import com.thebluealliance.android.tv.ui.theme.TbaTvTheme

private const val SCREEN_FADE_MILLIS = 220

private val screenFade =
    fadeIn(tween(SCREEN_FADE_MILLIS)) togetherWith fadeOut(tween(SCREEN_FADE_MILLIS))

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TbaTvTheme {
                val backStack = rememberNavBackStack(Screen.Feed)
                // Activity-scoped (created outside NavDisplay), so the feed keeps its data — no
                // skeleton flash — across a trip to About. AboutViewModel is activity-scoped too
                // (no ViewModelStore entry decorator), so About keeps its loaded contributors.
                val viewModel: EventsViewModel = viewModel(factory = EventsViewModel.Factory)
                // Hoisted out of the feed entry so Back lands on ⓘ instead of re-focusing card one.
                val feedFocusState = remember { FeedFocusState() }
                // NavDisplay's default saveable-state decorator keeps the feed's column and per-row
                // scroll positions while About is on top. Its AnimatedContent composes both screens
                // mid-fade; focus is safe because each side requests focus from onPlaced, i.e. only
                // once its target is laid out.
                NavDisplay(
                    backStack = backStack,
                    // NavDisplay only handles Back while there's an entry to pop, so Back from the
                    // feed falls through to the launcher (TV-DB). The size check guards the stale
                    // enabled-state case NavDisplay documents: never pop the root.
                    onBack = {
                        if (backStack.size > 1) {
                            if (backStack.removeLastOrNull() == Screen.About) {
                                feedFocusState.onAboutClosed()
                            }
                        }
                    },
                    transitionSpec = { screenFade },
                    popTransitionSpec = { screenFade },
                    predictivePopTransitionSpec = { screenFade },
                    entryProvider =
                        entryProvider {
                            entry<Screen.Feed> {
                                EventsScreen(
                                    viewModel = viewModel,
                                    focusState = feedFocusState,
                                    // A second OK press mid-fade must not stack a second About.
                                    onAboutClick = {
                                        if (backStack.lastOrNull() != Screen.About) {
                                            backStack.add(Screen.About)
                                        }
                                    },
                                )
                            }
                            entry<Screen.About> { AboutScreen() }
                        },
                )
            }
        }
    }
}
