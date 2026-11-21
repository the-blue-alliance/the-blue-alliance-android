package com.thebluealliance.android.tv.ui.theme

import androidx.compose.runtime.Composable
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.darkColorScheme
import com.thebluealliance.android.core.theme.TbaDarkColorTokens as Dark

// The shared TBA scheme (:core-theme), the same one the phone and Wear apps use. TV is dark-only.
// tv-material's border / borderVariant are Material 3's outline / outlineVariant.
private val TbaColorScheme =
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
        border = Dark.Outline,
        borderVariant = Dark.OutlineVariant,
        scrim = Dark.Scrim,
    )

@Composable
fun TbaTvTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = TbaColorScheme,
        typography = TbaTypography,
        content = content,
    )
}
