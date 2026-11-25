package com.thebluealliance.android.ui.common

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * A year picker's selection. Defaults worked out after launch (TBA's status, a team's seasons)
 * apply only until the user picks a year, so a slow fetch never overrides their choice.
 */
class SeasonSelection(
    initial: Int,
) {
    private val _year = MutableStateFlow(initial)
    val year: StateFlow<Int> = _year.asStateFlow()

    private var userPicked = false

    /** Whether the user has picked a year. */
    val hasUserPick: Boolean get() = userPicked

    /** Applies the user's pick and ignores every later default. */
    fun pick(year: Int) {
        userPicked = true
        _year.value = year
    }

    /** Whether a default of [year] would move the selection: only until the user picks. */
    fun wouldTakeDefault(year: Int): Boolean = !userPicked && _year.value != year

    /** Moves to the default [year] unless the user has picked; returns whether it moved. */
    fun offerDefault(year: Int): Boolean =
        wouldTakeDefault(year).also { if (it) _year.value = year }
}
