package com.thebluealliance.android.tv.ui.common

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.BringIntoViewSpec
import androidx.compose.foundation.gestures.LocalBringIntoViewSpec
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.layout.onPlaced

// Focus helpers adapted from the official TvMaterialCatalog sample (Apache-2.0):
// https://github.com/android/tv-samples/tree/main/TvMaterialCatalog

/** Conditional modifier — applies [ifTrueModifier] when [condition], else [elseModifier]. */
fun Modifier.ifElse(
    condition: Boolean,
    ifTrueModifier: Modifier,
    elseModifier: Modifier = Modifier,
): Modifier = then(if (condition) ifTrueModifier else elseModifier)

/**
 * Pivots focused items to a consistent position within scrollable containers so D-pad
 * focus doesn't sit flush against the viewport edge — there's always a peek of the
 * neighbouring item. Applies to every scrollable in [content] via LocalBringIntoViewSpec.
 * Edge items clamp naturally (the first item stays at the start, the last at the end).
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PositionFocusedItemInLazyLayout(
    parentFraction: Float = 0.3f,
    childFraction: Float = 0f,
    content: @Composable () -> Unit,
) {
    val bringIntoViewSpec =
        remember(parentFraction, childFraction) {
            object : BringIntoViewSpec {
                override fun calculateScrollDistance(
                    offset: Float,
                    size: Float,
                    containerSize: Float,
                ): Float {
                    val childSmallerThanParent = size <= containerSize
                    val initialTargetForLeadingEdge =
                        parentFraction * containerSize - (childFraction * size)
                    val spaceAvailableToShowItem = containerSize - initialTargetForLeadingEdge
                    val targetForLeadingEdge =
                        if (childSmallerThanParent && spaceAvailableToShowItem < size) {
                            containerSize - size
                        } else {
                            initialTargetForLeadingEdge
                        }
                    return offset - targetForLeadingEdge
                }
            }
        }
    CompositionLocalProvider(
        LocalBringIntoViewSpec provides bringIntoViewSpec,
        content = content,
    )
}

/** Requests focus the first time this node is placed — more reliable than LaunchedEffect(Unit). */
@Composable
fun Modifier.requestFocusOnFirstGainingVisibility(): Modifier {
    val focusRequester = remember { FocusRequester() }
    return focusRequester(focusRequester)
        .onFirstGainingVisibility { runCatching { focusRequester.requestFocus() } }
}

/**
 * Requests focus the first time this node is placed, gated on a caller-hoisted [isVisible] guard.
 * Mirrors JetStream's Modifier.focusOnInitialVisibility. Hoist [isVisible] to the screen (not the
 * list item): the guard is what stops a recycled item from re-grabbing focus and snapping the list
 * back to the top when it scrolls off and reappears.
 */
@Composable
fun Modifier.focusOnInitialVisibility(isVisible: MutableState<Boolean>): Modifier {
    val focusRequester = remember { FocusRequester() }
    return focusRequester(focusRequester)
        .onPlaced {
            if (!isVisible.value) {
                isVisible.value = true
                runCatching { focusRequester.requestFocus() }
            }
        }
}

@Composable
private fun Modifier.onFirstGainingVisibility(onGainingVisibility: () -> Unit): Modifier {
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(isVisible) { if (isVisible) onGainingVisibility() }
    return onPlaced { isVisible = true }
}
