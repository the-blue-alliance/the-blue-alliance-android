package com.thebluealliance.android.navigation

import androidx.compose.animation.ContentTransform
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.navigation3.ui.NavDisplay

object Transitions {
    private val tabTransition =
            fadeIn(tween(220, delayMillis = 90)) togetherWith fadeOut(tween(90))

    /**
     * Default transition for top-level tab transitions. Fades between the contents
     */
    val topLevelTransitionSpec = NavDisplay.transitionSpec {
        tabTransition
    } + NavDisplay.popTransitionSpec {
        tabTransition
    } + NavDisplay.predictivePopTransitionSpec {
        tabTransition
    }
}
