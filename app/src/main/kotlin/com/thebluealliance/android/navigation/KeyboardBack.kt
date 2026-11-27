package com.thebluealliance.android.navigation

import androidx.activity.OnBackPressedDispatcher

/**
 * The Alt+Left shortcut: Back within the app only. It goes back when something in the app
 * handles Back (a screen to pop, another tab to return to, an open pane), and otherwise does
 * nothing, as a browser's Back does on its first page. System Back still leaves the app there.
 * Returns whether it went back.
 */
internal fun OnBackPressedDispatcher.keyboardBack(): Boolean {
    if (!hasEnabledCallbacks()) return false
    onBackPressed()
    return true
}
