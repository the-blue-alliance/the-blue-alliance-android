package com.thebluealliance.android.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

/**
 * [PaddingValues] with the bottom dropped, for a container that keeps its background down to the
 * window's bottom edge and hands the bottom inset to its scrolling content instead.
 */
fun PaddingValues.withoutBottom(): PaddingValues = WithoutBottom(this)

/** Only the bottom of [PaddingValues], for the scrolling content under a [withoutBottom] container. */
fun PaddingValues.onlyBottom(): PaddingValues = PaddingValues(bottom = calculateBottomPadding())

private class WithoutBottom(
    private val padding: PaddingValues,
) : PaddingValues {
    override fun calculateLeftPadding(layoutDirection: LayoutDirection): Dp =
        padding.calculateLeftPadding(layoutDirection)

    override fun calculateTopPadding(): Dp = padding.calculateTopPadding()

    override fun calculateRightPadding(layoutDirection: LayoutDirection): Dp =
        padding.calculateRightPadding(layoutDirection)

    override fun calculateBottomPadding(): Dp = 0.dp

    override fun equals(other: Any?): Boolean = other is WithoutBottom && other.padding == padding

    override fun hashCode(): Int = padding.hashCode()
}

/**
 * Insets for a top-level screen's Scaffold content: the sides, and the bottom when no bottom bar
 * takes it (the navigation frame consumes it then). Content pads by it rather than stopping above it,
 * so backgrounds reach the window's bottom edge behind the gesture bar.
 */
val TopLevelContentWindowInsets: WindowInsets
    @Composable get() =
        WindowInsets.safeDrawing.only(
            WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom,
        )
