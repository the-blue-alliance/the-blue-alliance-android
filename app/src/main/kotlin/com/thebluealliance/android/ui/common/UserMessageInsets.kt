package com.thebluealliance.android.ui.common

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.BringIntoViewSpec
import androidx.compose.foundation.gestures.LocalBringIntoViewSpec
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection

/**
 * The bottom of the window that a snackbar with buttons covers, as an inset screens add to their
 * content padding (see [com.thebluealliance.android.ui.components.TopLevelContentWindowInsets]):
 * lists scroll their last row clear of it while backgrounds still reach the window's edge.
 * Zero while no such snackbar shows. [below] is the bottom inset the frame already took for the
 * bottom bar, if it shows (see [snackbarContentInset]).
 */
internal class UserMessageInsets(
    private val below: WindowInsets? = null,
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

    override fun getBottom(density: Density) =
        snackbarContentInset(coveredPx(), below?.getBottom(density) ?: 0)
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
    return if (covered <= 0f || above <= 0f || size > above) {
        default(offset, size, containerSize)
    } else {
        default(offset, size, above)
    }
}

/** [parent], bringing focused items into view above the snackbar (see [scrollDistanceAbove]). */
@OptIn(ExperimentalFoundationApi::class)
internal class AboveSnackbarBringIntoViewSpec(
    val parent: BringIntoViewSpec,
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

/**
 * This spec without the snackbar's room. [LocalBringIntoViewSpec] has no axis, and the snackbar
 * covers the bottom of the screen, not the end of a row.
 */
@OptIn(ExperimentalFoundationApi::class)
internal fun BringIntoViewSpec.ignoringSnackbar(): BringIntoViewSpec =
    (this as? AboveSnackbarBringIntoViewSpec)?.parent ?: this

/**
 * [content] with Compose's own bring-into-view, for a row that scrolls sideways (tabs, chips): its
 * focused items scroll into the whole row, not into a row shortened by the snackbar's height.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SidewaysBringIntoView(content: @Composable () -> Unit) {
    CompositionLocalProvider(
        LocalBringIntoViewSpec provides LocalBringIntoViewSpec.current.ignoringSnackbar(),
        content = content,
    )
}
