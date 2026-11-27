package com.thebluealliance.android.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.LocalRippleConfiguration
import androidx.compose.material3.LocalRippleThemeConfiguration
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RippleConfiguration
import androidx.compose.material3.RippleDefaults
import androidx.compose.material3.RippleThemeConfiguration
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.thebluealliance.android.core.theme.TbaBrandColors
import com.thebluealliance.android.core.theme.TbaPaletteTokens
import com.thebluealliance.android.core.theme.TbaDarkColorTokens as Dark
import com.thebluealliance.android.core.theme.TbaLightColorTokens as Light

// The shared TBA scheme (:core-theme), the same one the TV and Wear apps use: every Material 3
// role, generated from the brand seed #3F51B5. The brand blue itself is primaryContainer in both
// light and dark, which is what the top app bar and tab row use.

/**
 * Brand surfaces use fixed TBA brand colors with white content in both light and dark mode:
 * - The bars (top app bar, tab row, events week chips, widget-config header) are primaryContainer,
 *   which the generated scheme makes exactly TBA Blue #3F51B5 in both modes.
 * - Section and table headers are [BrandHeaderContainer], TBA's Indigo 400 header blue.
 * - Content on either is [OnBrand], white. onPrimaryContainer / secondaryContainer are muted by
 *   design and read with less contrast on these surfaces.
 * - Secondary content and outlines on the Blue bars are [OnBrandMuted] and [OnBrandOutline].
 *   They are for TBA Blue only, not [BrandHeaderContainer]: on the lighter header blue,
 *   [OnBrandMuted] falls below 4.5:1.
 */
val BrandHeaderContainer: Color = TbaBrandColors.HeaderBlue
val OnBrand: Color = TbaBrandColors.OnBlue
val OnBrandMuted: Color = TbaBrandColors.OnBlueMuted
val OnBrandOutline: Color = TbaBrandColors.OnBlueOutline

/**
 * The strip painted behind the status bar while the navigation rail shows: Neutral10 in both
 * modes, so the status bar is its own band above the rail and the blue bar. White icons, 17.2:1.
 */
val StatusStripGray: Color = TbaPaletteTokens.Neutral10

internal val TbaLightColorScheme =
    lightColorScheme(
        primary = Light.Primary,
        onPrimary = Light.OnPrimary,
        primaryContainer = Light.PrimaryContainer,
        onPrimaryContainer = Light.OnPrimaryContainer,
        inversePrimary = Light.InversePrimary,
        secondary = Light.Secondary,
        onSecondary = Light.OnSecondary,
        secondaryContainer = Light.SecondaryContainer,
        onSecondaryContainer = Light.OnSecondaryContainer,
        tertiary = Light.Tertiary,
        onTertiary = Light.OnTertiary,
        tertiaryContainer = Light.TertiaryContainer,
        onTertiaryContainer = Light.OnTertiaryContainer,
        background = Light.Background,
        onBackground = Light.OnBackground,
        surface = Light.Surface,
        onSurface = Light.OnSurface,
        surfaceVariant = Light.SurfaceVariant,
        onSurfaceVariant = Light.OnSurfaceVariant,
        surfaceTint = Light.SurfaceTint,
        inverseSurface = Light.InverseSurface,
        inverseOnSurface = Light.InverseOnSurface,
        error = Light.Error,
        onError = Light.OnError,
        errorContainer = Light.ErrorContainer,
        onErrorContainer = Light.OnErrorContainer,
        outline = Light.Outline,
        outlineVariant = Light.OutlineVariant,
        scrim = Light.Scrim,
        surfaceBright = Light.SurfaceBright,
        surfaceContainer = Light.SurfaceContainer,
        surfaceContainerHigh = Light.SurfaceContainerHigh,
        surfaceContainerHighest = Light.SurfaceContainerHighest,
        surfaceContainerLow = Light.SurfaceContainerLow,
        surfaceContainerLowest = Light.SurfaceContainerLowest,
        surfaceDim = Light.SurfaceDim,
        primaryFixed = Light.PrimaryFixed,
        primaryFixedDim = Light.PrimaryFixedDim,
        onPrimaryFixed = Light.OnPrimaryFixed,
        onPrimaryFixedVariant = Light.OnPrimaryFixedVariant,
        secondaryFixed = Light.SecondaryFixed,
        secondaryFixedDim = Light.SecondaryFixedDim,
        onSecondaryFixed = Light.OnSecondaryFixed,
        onSecondaryFixedVariant = Light.OnSecondaryFixedVariant,
        tertiaryFixed = Light.TertiaryFixed,
        tertiaryFixedDim = Light.TertiaryFixedDim,
        onTertiaryFixed = Light.OnTertiaryFixed,
        onTertiaryFixedVariant = Light.OnTertiaryFixedVariant,
    )

internal val TbaDarkColorScheme =
    darkColorScheme(
        primary = Dark.Primary,
        onPrimary = Dark.OnPrimary,
        primaryContainer = Dark.PrimaryContainer,
        onPrimaryContainer = Dark.OnPrimaryContainer,
        inversePrimary = Dark.InversePrimary,
        secondary = Dark.Secondary,
        onSecondary = Dark.OnSecondary,
        secondaryContainer = Dark.SecondaryContainer,
        onSecondaryContainer = Dark.OnSecondaryContainer,
        tertiary = Dark.Tertiary,
        onTertiary = Dark.OnTertiary,
        tertiaryContainer = Dark.TertiaryContainer,
        onTertiaryContainer = Dark.OnTertiaryContainer,
        background = Dark.Background,
        onBackground = Dark.OnBackground,
        surface = Dark.Surface,
        onSurface = Dark.OnSurface,
        surfaceVariant = Dark.SurfaceVariant,
        onSurfaceVariant = Dark.OnSurfaceVariant,
        surfaceTint = Dark.SurfaceTint,
        inverseSurface = Dark.InverseSurface,
        inverseOnSurface = Dark.InverseOnSurface,
        error = Dark.Error,
        onError = Dark.OnError,
        errorContainer = Dark.ErrorContainer,
        onErrorContainer = Dark.OnErrorContainer,
        outline = Dark.Outline,
        outlineVariant = Dark.OutlineVariant,
        scrim = Dark.Scrim,
        surfaceBright = Dark.SurfaceBright,
        surfaceContainer = Dark.SurfaceContainer,
        surfaceContainerHigh = Dark.SurfaceContainerHigh,
        surfaceContainerHighest = Dark.SurfaceContainerHighest,
        surfaceContainerLow = Dark.SurfaceContainerLow,
        surfaceContainerLowest = Dark.SurfaceContainerLowest,
        surfaceDim = Dark.SurfaceDim,
        primaryFixed = Dark.PrimaryFixed,
        primaryFixedDim = Dark.PrimaryFixedDim,
        onPrimaryFixed = Dark.OnPrimaryFixed,
        onPrimaryFixedVariant = Dark.OnPrimaryFixedVariant,
        secondaryFixed = Dark.SecondaryFixed,
        secondaryFixedDim = Dark.SecondaryFixedDim,
        onSecondaryFixed = Dark.OnSecondaryFixed,
        onSecondaryFixedVariant = Dark.OnSecondaryFixedVariant,
        tertiaryFixed = Dark.TertiaryFixed,
        tertiaryFixedDim = Dark.TertiaryFixedDim,
        onTertiaryFixed = Dark.OnTertiaryFixed,
        onTertiaryFixedVariant = Dark.OnTertiaryFixedVariant,
    )

/**
 * Keyboard focus draws Material's inset focus ring (a secondary outer stroke over an onSecondary
 * inner stroke) on every ripple, instead of the faint 10% focus state layer that disappears on
 * the brand bars. Clickables are only focusable outside touch mode, so touch never shows it.
 */
internal val TbaRippleThemeConfiguration: RippleThemeConfiguration =
    RippleDefaults.InsetFocusRingThemeConfiguration

/**
 * How far [TbaRippleThemeConfiguration]'s focus ring reaches in from a target's edge. Content
 * needs more inner padding than this, or the ring draws over it.
 */
internal val FocusRingDepth: Dp =
    when (val focus = TbaRippleThemeConfiguration.focus) {
        is RippleThemeConfiguration.Focus.InsetRing ->
            maxOf(
                focus.outerStrokeInset + focus.outerStrokeWidth,
                focus.innerStrokeInset + focus.innerStrokeWidth,
            )
        else -> 0.dp
    }

/**
 * Focus ring colors for content on a brand surface of [container] color. The theme's default ring
 * (secondary over onSecondary) nearly matches TBA Blue, so only its inner stroke shows there. On a
 * brand surface the outer stroke is [OnBrand] and the inner stroke is the surface's own color: one
 * of the two contrasts with whatever is behind it, whether that's the bar or a white selected chip.
 */
internal fun brandFocusRing(container: Color): RippleConfiguration =
    RippleConfiguration(
        focus =
            RippleConfiguration.Focus.InsetRing(
                outerStrokeColor = OnBrand,
                innerStrokeColor = container,
            ),
    )

/**
 * Draws the focus ring in brand colors (see [brandFocusRing]) for clickables in [content], which
 * sits on a brand surface of [container] color. Menus that pop up from inside [content] should
 * wrap their items in [SurfaceFocusRing], since a popup inherits this but draws on a surface.
 */
@Composable
fun BrandFocusRing(
    container: Color,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalRippleConfiguration provides brandFocusRing(container),
        content = content,
    )
}

/** Restores the theme's focus ring colors inside a [BrandFocusRing], e.g. in a popup menu. */
@Composable
fun SurfaceFocusRing(content: @Composable () -> Unit) {
    CompositionLocalProvider(
        LocalRippleConfiguration provides RippleConfiguration(),
        content = content,
    )
}

@Composable
fun TBATheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme =
        when {
            dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                val context = LocalContext.current
                if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
            }
            darkTheme -> TbaDarkColorScheme
            else -> TbaLightColorScheme
        }

    CompositionLocalProvider(
        LocalAllianceColors provides if (darkTheme) DarkAllianceColors else LightAllianceColors,
        LocalRippleThemeConfiguration provides TbaRippleThemeConfiguration,
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            content = content,
        )
    }
}
