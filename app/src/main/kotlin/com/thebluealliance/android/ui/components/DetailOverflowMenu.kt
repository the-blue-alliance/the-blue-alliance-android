package com.thebluealliance.android.ui.components

/**
 * Whether a detail screen's More options menu has anything in it: Share needs the loaded
 * entity, and Add to home screen needs a launcher that can pin shortcuts.
 */
internal fun hasDetailOverflowItems(
    entityLoaded: Boolean,
    canPinShortcuts: Boolean,
): Boolean = entityLoaded || canPinShortcuts
