package com.thebluealliance.android.navigation

import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.togetherWith
import androidx.navigation3.ui.NavDisplay
import com.thebluealliance.android.ui.theme.TBAMotionTokens

object Transitions {
    private val tabTransition =
        fadeIn(TBAMotionTokens.fastEffectsSpec()) togetherWith
            ExitTransition.None

    /**
     * Default transition for top-level tab transitions. Fades between the contents
     */
    val topLevelTransitionSpec =
        NavDisplay.transitionSpec {
            tabTransition
        } +
            NavDisplay.popTransitionSpec {
                tabTransition
            } +
            NavDisplay.predictivePopTransitionSpec {
                tabTransition
            }
}
