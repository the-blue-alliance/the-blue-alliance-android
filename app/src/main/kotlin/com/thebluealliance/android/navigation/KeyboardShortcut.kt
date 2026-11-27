package com.thebluealliance.android.navigation

import android.view.KeyEvent
import com.thebluealliance.android.ui.TOP_LEVEL_DESTINATIONS

/** What an app-wide keyboard shortcut does, whichever keys pressed it. */
sealed interface ShortcutAction {
    data object Search : ShortcutAction

    /** Refreshes the visible screen, as pull-to-refresh does; screens without one ignore it. */
    data object Refresh : ShortcutAction

    /** Back within the app; never leaves it (see [keyboardBack]). */
    data object Back : ShortcutAction

    /** Opens [TOP_LEVEL_DESTINATIONS] at [index], as tapping it in the bar or rail does. */
    data class SwitchTab(
        val index: Int,
    ) : ShortcutAction
}

/**
 * An app-wide keyboard shortcut: [keyCode] with exactly [modifiers] (`KeyEvent.META_*_ON`, or 0)
 * held. Handled once in [com.thebluealliance.android.MainActivity] and listed in the system
 * Keyboard Shortcuts Helper (Meta+/) under [label].
 */
data class KeyboardShortcut(
    val label: String,
    val keyCode: Int,
    val modifiers: Int,
    val action: ShortcutAction,
) {
    companion object {
        private const val CTRL = KeyEvent.META_CTRL_ON

        private val TAB_KEYS =
            listOf(
                KeyEvent.KEYCODE_1,
                KeyEvent.KEYCODE_2,
                KeyEvent.KEYCODE_3,
                KeyEvent.KEYCODE_4,
                KeyEvent.KEYCODE_5,
                KeyEvent.KEYCODE_6,
                KeyEvent.KEYCODE_7,
                KeyEvent.KEYCODE_8,
                KeyEvent.KEYCODE_9,
            )

        /** Shortcuts that move around the app, in the order the helper lists them. */
        val navigation: List<KeyboardShortcut> =
            listOf(
                KeyboardShortcut("Search", KeyEvent.KEYCODE_F, CTRL, ShortcutAction.Search),
                KeyboardShortcut(
                    "Back",
                    KeyEvent.KEYCODE_DPAD_LEFT,
                    KeyEvent.META_ALT_ON,
                    ShortcutAction.Back,
                ),
            ) +
                TOP_LEVEL_DESTINATIONS.zip(TAB_KEYS).mapIndexed { index, (destination, key) ->
                    KeyboardShortcut(destination.label, key, CTRL, ShortcutAction.SwitchTab(index))
                }

        /** Shortcuts that act on the visible screen. */
        val screen: List<KeyboardShortcut> =
            listOf(
                KeyboardShortcut("Refresh", KeyEvent.KEYCODE_R, CTRL, ShortcutAction.Refresh),
                // Newer helpers leave out keys without a modifier, so only some list F5.
                KeyboardShortcut("Refresh", KeyEvent.KEYCODE_F5, 0, ShortcutAction.Refresh),
            )

        /** Every shortcut. Meta chords belong to the system, so these use Ctrl, Alt, or none. */
        val all: List<KeyboardShortcut> = navigation + screen

        /** The action for [keyCode] pressed with exactly a shortcut's modifiers, if any. */
        fun match(
            keyCode: Int,
            metaState: Int,
        ): ShortcutAction? {
            val held = normalize(metaState)
            return all.firstOrNull { it.keyCode == keyCode && it.modifiers == held }?.action
        }

        /** Folds left/right variants together and drops lock keys, so Right Ctrl counts as Ctrl. */
        private fun normalize(metaState: Int): Int {
            var held = 0
            if (metaState and KeyEvent.META_CTRL_MASK != 0) held = held or CTRL
            if (metaState and KeyEvent.META_SHIFT_MASK != 0) held = held or KeyEvent.META_SHIFT_ON
            if (metaState and KeyEvent.META_ALT_MASK != 0) held = held or KeyEvent.META_ALT_ON
            if (metaState and KeyEvent.META_META_MASK != 0) held = held or KeyEvent.META_META_ON
            return held
        }
    }
}
