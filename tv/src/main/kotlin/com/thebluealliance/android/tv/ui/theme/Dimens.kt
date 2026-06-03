package com.thebluealliance.android.tv.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp

// Shared design tokens. Centralising the few values that every screen repeats keeps the 10-foot
// look consistent and the magic numbers in one place. Adapted from JetStream's ParentPadding +
// JetStreamFocusTheme, flattened: our layout is a single feed (no nested parent/child panes), so a
// single horizontal inset replaces rememberChildPadding.

// 10-foot overscan-safe margins. Content indents by these so nothing rides the physical TV bezel.
val TbaScreenHPadding = 48.dp
val TbaOverscanTopPadding = 27.dp
val TbaListBottomPadding = 48.dp

// Focus language: a bright border (+ scale where the viewport allows it), never a bare colour fill
// as the only cue. One width and the two surface shapes live here so every focusable surface reads
// the same. JetStream uses 3dp; we hold 2dp to match the existing card weight.
val TbaFocusBorderWidth = 2.dp
val TbaCardShape = RoundedCornerShape(14.dp)
val TbaRowShape = RoundedCornerShape(10.dp)

// Icon-button geometry (the top-right About control).
val TbaIconButtonSize = 40.dp
val TbaIconSize = 24.dp
