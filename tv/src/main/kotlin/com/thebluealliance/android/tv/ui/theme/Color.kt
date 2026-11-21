package com.thebluealliance.android.tv.ui.theme

import androidx.compose.ui.graphics.Color

// TV-only fixed colors (card art, the generic webcast dot). Theme roles and the brand blue live in :core-theme.
val TbaBlueBright = Color(0xFF5C6BC0)

// Event placeholder art: a muted TBA-blue corner fading to just above the page background. The
// luminance step is deliberately small — big full-saturation gradients band on TV panels.
val TbaArtGradientStart = Color(0xFF1F2A6B)
val TbaArtGradientEnd = Color(0xFF10141C)

// Lamp watermark tint on the art: a light TBA indigo so it reads as brand, not a grey shape.
val TbaArtWatermark = Color(0xFF7986CB)

