package com.thebluealliance.android.tv.ui.theme

import androidx.compose.ui.graphics.Color

// Fixed brand and platform colors. Theme roles live in Theme.kt's generated scheme.
val TbaBlue = Color(0xFF3F51B5)
val TbaBlueBright = Color(0xFF5C6BC0)

// Event placeholder art: a muted TBA-blue corner fading to just above the page background. The
// luminance step is deliberately small — big full-saturation gradients band on TV panels.
val TbaArtGradientStart = Color(0xFF1F2A6B)
val TbaArtGradientEnd = Color(0xFF10141C)

// Lamp watermark tint on the art: a light TBA indigo so it reads as brand, not a grey shape.
val TbaArtWatermark = Color(0xFF7986CB)

