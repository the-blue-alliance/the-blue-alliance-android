package com.thebluealliance.android.ui.theme

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.spring

/**
 * M3 Expressive motion tokens, mirroring `ExpressiveMotionTokens` from
 * `androidx.compose.material3.tokens` (which is internal in material3 1.4.0).
 *
 * Values are copied verbatim from the library source (v0_14_0).
 * Effects specs are identical between Standard and Expressive schemes.
 * TODO: Replace with `MaterialTheme.motionScheme` once it becomes public API.
 */
object TBAMotionTokens {
    // M3 emphasized easing curves (from MotionTokens v0_103)
    val EmphasizedDecelerateEasing = CubicBezierEasing(0.05f, 0.7f, 0.1f, 1.0f)
    val EmphasizedAccelerateEasing = CubicBezierEasing(0.3f, 0.0f, 0.8f, 0.15f)

    // --- Expressive motion scheme spring specs (from ExpressiveMotionTokens v0_14_0) ---

    fun <T> defaultSpatialSpec(): FiniteAnimationSpec<T> =
        spring(dampingRatio = 0.8f, stiffness = 380.0f)

    fun <T> fastSpatialSpec(): FiniteAnimationSpec<T> =
        spring(dampingRatio = 0.6f, stiffness = 800.0f)

    fun <T> slowSpatialSpec(): FiniteAnimationSpec<T> =
        spring(dampingRatio = 0.8f, stiffness = 200.0f)

    fun <T> defaultEffectsSpec(): FiniteAnimationSpec<T> =
        spring(dampingRatio = 1.0f, stiffness = 1600.0f)

    fun <T> fastEffectsSpec(): FiniteAnimationSpec<T> =
        spring(dampingRatio = 1.0f, stiffness = 3800.0f)

    fun <T> slowEffectsSpec(): FiniteAnimationSpec<T> =
        spring(dampingRatio = 1.0f, stiffness = 800.0f)
}
