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

// Event cards use a heavier ring than the rest of the app: it sits on busy art, not a flat fill.
val TbaCardFocusBorderWidth = 3.dp

// 8dp matches the tv-material Card default and Google TV; larger radii read as phone UI.
val TbaCardShape = RoundedCornerShape(8.dp)
val TbaRowShape = RoundedCornerShape(10.dp)

// Android TV "standard card": a 3-up width from the 960dp TV grid (3 cards + a peek of the 4th at
// a 48dp inset and 20dp gaps), 16:9 art, and the text block 8dp below the art.
val TbaCardWidth = 268.dp
val TbaCardArtAspectRatio = 16f / 9f
val TbaCardTextGap = 8.dp
val TbaCardSpacing = 20.dp

// Icon-button geometry (the top-right About control).
val TbaIconButtonSize = 40.dp
val TbaIconSize = 24.dp
