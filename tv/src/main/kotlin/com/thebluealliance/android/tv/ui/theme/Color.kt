package com.thebluealliance.android.tv.ui.theme

import androidx.compose.ui.graphics.Color

// The Blue Alliance brand + 10-foot dark palette
val TbaBlue = Color(0xFF3F51B5)
val TbaBlueBright = Color(0xFF5C6BC0)

// Event placeholder art: a muted TBA-blue corner fading to just above the page background. The
// luminance step is deliberately small — big full-saturation gradients band on TV panels.
val TbaArtGradientStart = Color(0xFF1F2A6B)
val TbaArtGradientEnd = Color(0xFF10141C)

// Lamp watermark tint on the art: a light TBA indigo so it reads as brand, not a grey shape.
val TbaArtWatermark = Color(0xFF7986CB)

// Card focus ring. White, because a blue ring disappears against the blue placeholder art.
val TbaCardFocusRing = Color.White
val TbaBackground = Color(0xFF0E1116)
val TbaSurface = Color(0xFF1A1F2B)
val TbaSurfaceVariant = Color(0xFF252B3A)
val LiveRed = Color(0xFFFF3B30)
val TextPrimary = Color(0xFFFFFFFF)
val TextSecondary = Color(0xFFB0B6C0)
val YouTubeRed = Color(0xFFFF0000)
val TwitchPurple = Color(0xFF9146FF)

// Brightened brand tint used for badge *text* on dark translucent fills, so the small label clears
// WCAG AA contrast (the full-saturation brand colors do not at 13sp).
val LiveOnChip = Color(0xFFFF8A80)
