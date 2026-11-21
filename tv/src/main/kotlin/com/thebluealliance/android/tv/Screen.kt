package com.thebluealliance.android.tv

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

/** The TV app's destinations. [Feed] is always the root of the back stack. */
sealed interface Screen : NavKey {
    @Serializable data object Feed : Screen

    @Serializable data object About : Screen
}
