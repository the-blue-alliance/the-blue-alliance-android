package com.thebluealliance.android.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

private val ThumbWidth = 4.dp
private val ThumbHeight = 48.dp
private val TouchTargetWidth = 24.dp

@Composable
fun FastScrollbar(
    listState: LazyListState,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    val totalItems by remember { derivedStateOf { listState.layoutInfo.totalItemsCount } }
    val thumbFraction by remember {
        derivedStateOf {
            if (totalItems == 0) 0f
            else listState.firstVisibleItemIndex.toFloat() / totalItems
        }
    }
    val isScrolling by remember { derivedStateOf { listState.isScrollInProgress } }

    var isDragging by remember { mutableStateOf(false) }

    val alpha = remember { Animatable(0f) }
    LaunchedEffect(isScrolling, isDragging) {
        if (isScrolling || isDragging) {
            alpha.animateTo(1f, tween(150))
        } else {
            delay(1500)
            alpha.animateTo(0f, tween(300))
        }
    }

    var trackHeightPx by remember { mutableFloatStateOf(0f) }

    Box(modifier = modifier.fillMaxSize()) {
        content()

        if (totalItems > 0) {
            // Invisible touch target: full-height strip on the right edge for drag gestures
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .fillMaxHeight()
                    .width(TouchTargetWidth)
                    .onSizeChanged { trackHeightPx = it.height.toFloat() }
                    .pointerInput(totalItems) {
                        detectVerticalDragGestures(
                            onDragStart = { isDragging = true },
                            onDragEnd = { isDragging = false },
                            onDragCancel = { isDragging = false },
                            onVerticalDrag = { change, _ ->
                                change.consume()
                                val fraction = (change.position.y / trackHeightPx).coerceIn(0f, 1f)
                                val targetIndex = (fraction * totalItems).roundToInt()
                                    .coerceIn(0, (totalItems - 1).coerceAtLeast(0))
                                listState.requestScrollToItem(targetIndex)
                            },
                        )
                    },
            )

            // Visible thumb indicator
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(end = 4.dp)
                    .alpha(alpha.value)
                    .offset {
                        val thumbHeightPx = ThumbHeight.toPx()
                        val scrollableTrack = trackHeightPx - thumbHeightPx
                        val offsetY = (thumbFraction * scrollableTrack).coerceIn(0f, scrollableTrack)
                        IntOffset(0, offsetY.roundToInt())
                    }
                    .width(ThumbWidth)
                    .height(ThumbHeight)
                    .clip(RoundedCornerShape(ThumbWidth / 2))
                    .background(MaterialTheme.colorScheme.onSurfaceVariant),
            )
        }
    }
}
