package com.thebluealliance.android.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Row of ranking-point bonus indicators: filled when achieved, hollow when not. */
@Composable
fun RpDots(
    bonuses: List<Boolean>,
    achievedColor: Color,
    modifier: Modifier = Modifier,
    dotSize: Dp = 6.dp,
    gap: Dp = 2.dp,
) {
    val emptyColor = MaterialTheme.colorScheme.outline
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(gap),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        bonuses.forEach { achieved ->
            Canvas(modifier = Modifier.size(dotSize)) {
                if (achieved) {
                    drawCircle(
                        color = achievedColor,
                        radius = size.minDimension / 2,
                    )
                } else {
                    drawCircle(
                        color = emptyColor,
                        radius = size.minDimension / 2 - 1.dp.toPx(),
                        style = Stroke(width = 1.dp.toPx()),
                    )
                }
            }
        }
    }
}
