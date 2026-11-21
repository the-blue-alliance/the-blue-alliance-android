package com.thebluealliance.android.ui

import androidx.annotation.DrawableRes
import androidx.navigation3.runtime.NavKey
import com.thebluealliance.android.R
import com.thebluealliance.android.navigation.Screen

data class TopLevelDestination(
    val key: NavKey,
    val label: String,
    @DrawableRes val selectedIcon: Int,
    @DrawableRes val unselectedIcon: Int,
)

val TOP_LEVEL_DESTINATIONS =
    listOf(
        TopLevelDestination(
            Screen.Events(),
            "Events",
            R.drawable.ic_calendar_month_filled,
            R.drawable.ic_calendar_month,
        ),
        TopLevelDestination(
            Screen.Teams,
            "Teams",
            R.drawable.ic_groups_filled,
            R.drawable.ic_groups,
        ),
        TopLevelDestination(
            Screen.Districts,
            "Districts",
            R.drawable.ic_map_filled,
            R.drawable.ic_map,
        ),
        TopLevelDestination(Screen.More, "More", R.drawable.ic_menu, R.drawable.ic_menu),
    )
