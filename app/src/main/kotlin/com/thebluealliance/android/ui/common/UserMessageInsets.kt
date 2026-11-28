package com.thebluealliance.android.ui.common

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.BringIntoViewSpec
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection

/**
 * The bottom of the window that a snackbar with buttons covers, as an inset screens add to their
 * content padding (see [com.thebluealliance.android.ui.components.TopLevelContentWindowInsets]):
 * lists scroll their last row clear of it while backgrounds still reach the window's edge.
 * Zero while no such snackbar shows.
 */
internal class UserMessageInsets(
    private val coveredPx: () -> Int,
) : WindowInsets {
    override fun getLeft(
        density: Density,
        layoutDirection: LayoutDirection,
    ) = 0

    override fun getTop(density: Density) = 0

    override fun getRight(
        density: Density,
        layoutDirection: LayoutDirection,
    ) = 0

    override fun getBottom(density: Density) = coveredPx()
}

/** The window's [UserMessageInsets]; none outside the navigation frame. */
val LocalUserMessageInsets = staticCompositionLocalOf<WindowInsets> { WindowInsets(0, 0, 0, 0) }

/**
 * How far a scrolling container moves to bring an item [size] long at [offset] into view, keeping
 * it clear of the last [covered] pixels of its [containerSize], where the snackbar sits. Content
 * padding doesn't do this: Compose brings a focused item into the whole container, padding
 * included. An item too long to fit above [covered], such as a whole pager page, gets [default]'s
 * answer for the whole container, so pages and full-width rows never move sideways for it.
 */
internal fun scrollDistanceAbove(
    offset: Float,
    size: Float,
    containerSize: Float,
    covered: Float,
    default: (offset: Float, size: Float, containerSize: Float) -> Float,
): Float {
    val above = containerSize - covered
    return if (covered <= 0f || size > above) {
        default(offset, size, containerSize)
    } else {
        default(offset, size, above)
    }
}

/** [parent], bringing focused items into view above the snackbar (see [scrollDistanceAbove]). */
@OptIn(ExperimentalFoundationApi::class)
internal class AboveSnackbarBringIntoViewSpec(
    private val parent: BringIntoViewSpec,
    private val coveredPx: () -> Int,
) : BringIntoViewSpec {
    @Suppress("DEPRECATION")
    override val scrollAnimationSpec get() = parent.scrollAnimationSpec

    override fun calculateScrollDistance(
        offset: Float,
        size: Float,
        containerSize: Float,
    ): Float =
        scrollDistanceAbove(offset, size, containerSize, coveredPx().toFloat()) { o, s, c ->
            parent.calculateScrollDistance(o, s, c)
        }
}
