package com.thebluealliance.android.core.theme

import androidx.compose.ui.graphics.Color

/**
 * FRC alliance colors: Material 3 custom colors (color / onColor / container / onContainer per
 * side, light and dark), deliberately not theme roles so a scheme change never moves them.
 * https://m3.material.io/styles/color/advanced/define-new-colors
 *
 * Values are the ones the phone app shipped when it borrowed theme roles for alliances: red was
 * Material 3's baseline `error` palette, blue was the hand-set Indigo `primary` roles.
 */
object TbaAllianceColors {
    object Light {
        val Red = Color(0xFFB3261E)
        val OnRed = Color(0xFFFFFFFF)
        val RedContainer = Color(0xFFF9DEDC)
        val OnRedContainer = Color(0xFF410E0B)
        val Blue = Color(0xFF3F51B5)
        val OnBlue = Color(0xFFFFFFFF)
        val BlueContainer = Color(0xFFC5CAE9)
        val OnBlueContainer = Color(0xFF1A237E)
    }

    object Dark {
        val Red = Color(0xFFF2B8B5)
        val OnRed = Color(0xFF601410)
        val RedContainer = Color(0xFF8C1D18)
        val OnRedContainer = Color(0xFFF9DEDC)
        val Blue = Color(0xFF9FA8DA)
        val OnBlue = Color(0xFF00174D)
        val BlueContainer = Color(0xFF303F9F)
        val OnBlueContainer = Color(0xFFC5CAE9)
    }

    /** FIRST's official team-avatar backgrounds; avatars are designed to sit on these. */
    val AvatarRed = Color(0xFFED1C24)
    val AvatarBlue = Color(0xFF0066B3)
}
