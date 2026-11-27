package com.thebluealliance.android.navigation

import android.view.KeyEvent

/**
 * App-wide keyboard shortcuts, handled once in [com.thebluealliance.android.MainActivity] and
 * listed in the system Keyboard Shortcuts Helper (Meta+/).
 *
 * Every shortcut is Ctrl plus one key: Android routes Ctrl chords the view tree leaves unhandled to
 * `Activity.onKeyShortcut`, while Meta chords belong to the system.
 */
enum class KeyboardShortcut(
    val label: String,
    val keyCode: Int,
) {
    Search("Search", KeyEvent.KEYCODE_F),
    ;

    companion object {
        private const val MODIFIER_MASK =
            KeyEvent.META_CTRL_MASK or
                KeyEvent.META_SHIFT_MASK or
                KeyEvent.META_ALT_MASK or
                KeyEvent.META_META_MASK

        /** The shortcut for [keyCode] pressed with Ctrl and no other modifier, if any. */
        fun match(
            keyCode: Int,
            metaState: Int,
        ): KeyboardShortcut? {
            val modifiers = metaState and MODIFIER_MASK
            val ctrlOnly = modifiers != 0 && (modifiers and KeyEvent.META_CTRL_MASK) == modifiers
            return if (ctrlOnly) entries.firstOrNull { it.keyCode == keyCode } else null
        }
    }
}
