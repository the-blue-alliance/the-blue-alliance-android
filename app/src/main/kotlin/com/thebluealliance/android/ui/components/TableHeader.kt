package com.thebluealliance.android.ui.components

import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput

/**
 * Makes a pinned table header a hit target without handling any input itself, so taps, hovers
 * and pinches on its plain labels stop there instead of reaching the row scrolled beneath it.
 * Its own clickable cells still get theirs.
 */
fun Modifier.blocksRowsBeneath(): Modifier = this.pointerInput(Unit) {}
