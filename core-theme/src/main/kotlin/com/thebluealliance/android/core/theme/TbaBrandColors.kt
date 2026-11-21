package com.thebluealliance.android.core.theme

import androidx.compose.ui.graphics.Color

/**
 * Fixed TBA brand colors: not theme roles, so they don't shift with the scheme or light/dark mode.
 * They match the web server's CSS variables (tba_variables.less) and STYLE_GUIDE.md.
 */
object TbaBrandColors {
    /** TBA Blue (Material Indigo 500): the seed, both schemes' primaryContainer, launcher icons. */
    val Blue = Color(0xFF3F51B5)

    /** Section and table headers (Material Indigo 400), a step lighter than the [Blue] app bar. */
    val HeaderBlue = Color(0xFF5C6BC0)

    /** Content on [Blue] and [HeaderBlue] (6.87:1 and 4.86:1). */
    val OnBlue = Color.White
}
