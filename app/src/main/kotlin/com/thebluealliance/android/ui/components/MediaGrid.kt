package com.thebluealliance.android.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.thebluealliance.android.domain.model.Media
import com.thebluealliance.android.ui.common.EmptyBox
import com.thebluealliance.android.ui.common.LoadingBox
import com.thebluealliance.android.ui.theme.TbaIcons
import com.thebluealliance.android.util.openUrl

/**
 * Data class for a media item to display in a media grid.
 */
data class MediaGridItem(
    val type: String,
    val foreignKey: String,
)

/**
 * Returns a thumbnail URL for a media item, or null if the type isn't supported. imgur tiles use
 * its 640px "l" thumbnail rather than the full-size original, which [mediaLinkUrl] opens.
 */
fun mediaUrl(
    type: String,
    foreignKey: String,
): String? =
    when (type) {
        "imgur" -> "https://i.imgur.com/${foreignKey}l.jpg"
        "cdphotothread" -> "https://www.chiefdelphi.com/media/img/$foreignKey"
        "instagram-image" -> "https://www.instagram.com/p/$foreignKey/media/"
        "youtube" -> "https://img.youtube.com/vi/$foreignKey/hqdefault.jpg"
        "grabcad" -> null
        "onshape" -> null
        else -> null
    }

/**
 * Returns the external link URL for a media item, or null if the type isn't supported.
 */
fun mediaLinkUrl(
    type: String,
    foreignKey: String,
): String? =
    when (type) {
        "imgur" -> "https://imgur.com/$foreignKey"
        "cdphotothread" -> "https://www.chiefdelphi.com/media/img/$foreignKey"
        "instagram-image" -> "https://www.instagram.com/p/$foreignKey/"
        "youtube" -> "https://www.youtube.com/watch?v=$foreignKey"
        else -> null
    }

/** Spoken label for the tile at zero-based [index] of [count], e.g. "Video 2 of 5". */
internal fun mediaTileDescription(
    type: String,
    index: Int,
    count: Int,
): String = "${if (type == "youtube") "Video" else "Photo"} ${index + 1} of $count"

/** Click label naming where tapping a tile goes. */
internal fun mediaClickLabel(type: String): String =
    if (type == "youtube") "Open in YouTube" else "Open full image"

/**
 * A single media item with thumbnail, optional play overlay for YouTube, and click-to-open.
 */
@Composable
fun MediaItem(
    type: String,
    foreignKey: String,
    index: Int,
    count: Int,
    modifier: Modifier = Modifier,
) {
    val url = mediaUrl(type, foreignKey) ?: return
    val linkUrl = mediaLinkUrl(type, foreignKey)
    val context = LocalContext.current

    Box(
        modifier =
            modifier
                .aspectRatio(1f)
                .clip(MaterialTheme.shapes.medium)
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .then(
                    if (linkUrl != null) {
                        Modifier.clickable(
                            onClickLabel = mediaClickLabel(type),
                            role = Role.Button,
                        ) { context.openUrl(linkUrl) }
                    } else {
                        Modifier
                    },
                ),
    ) {
        AsyncImage(
            model =
                ImageRequest
                    .Builder(LocalPlatformContext.current)
                    .data(url)
                    .crossfade(true)
                    .build(),
            contentDescription = mediaTileDescription(type, index, count),
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize(),
        )
        if (type == "youtube") {
            Box(
                modifier =
                    Modifier
                        .align(Alignment.Center)
                        .size(48.dp)
                        .background(
                            MaterialTheme.colorScheme.scrim.copy(alpha = 0.5f),
                            CircleShape,
                        ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    TbaIcons.PlayCircle,
                    // The tile's label already says it is a video.
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(48.dp),
                )
            }
        }
    }
}

/**
 * Renders media items in a 2-column grid layout using Rows.
 * Designed to be called inside a LazyColumn item{} block (not as a nested lazy layout).
 */
@Composable
fun MediaGridRow(
    items: List<MediaGridItem>,
    modifier: Modifier = Modifier,
) {
    items.chunked(2).forEachIndexed { rowIndex, rowItems ->
        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            rowItems.forEachIndexed { column, item ->
                MediaItem(
                    type = item.type,
                    foreignKey = item.foreignKey,
                    index = rowIndex * 2 + column,
                    count = items.size,
                    modifier = Modifier.weight(1f),
                )
            }
            if (rowItems.size == 1) {
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

/**
 * Minimum media tile width: 160dp keeps phones at two columns; 240dp on medium and wider windows
 * gives three columns at 840dp and five at 1280dp.
 */
internal fun mediaTileMinSize(windowSizeClass: WindowSizeClass): Dp =
    if (windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)) {
        240.dp
    } else {
        160.dp
    }

/**
 * A full-screen tab that displays a team's media in an adaptive grid sized by [mediaTileMinSize].
 * Shared between Team detail and Team@Event detail screens.
 */
@Composable
fun MediaTab(
    media: List<Media>?,
    innerPadding: PaddingValues = PaddingValues.Zero,
    emptyAction: (@Composable () -> Unit)? = null,
) {
    if (media == null) {
        LoadingBox(modifier = Modifier.padding(innerPadding))
        return
    }
    val filtered = media.filter { it.type != "avatar" }
    if (filtered.isEmpty()) {
        EmptyBox(
            modifier = Modifier.padding(innerPadding),
            message = "No media",
            action = emptyAction,
        )
        return
    }
    val gridItems =
        filtered.mapNotNull { item ->
            if (mediaUrl(item.type, item.foreignKey) != null) {
                MediaGridItem(type = item.type, foreignKey = item.foreignKey)
            } else {
                null
            }
        }
    LazyVerticalGrid(
        columns =
            GridCells.Adaptive(
                minSize = mediaTileMinSize(currentWindowAdaptiveInfo().windowSizeClass),
            ),
        modifier =
            Modifier
                .fillMaxSize()
                .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = innerPadding,
    ) {
        itemsIndexed(gridItems, key = { _, it -> "${it.type}_${it.foreignKey}" }) { index, item ->
            MediaItem(
                type = item.type,
                foreignKey = item.foreignKey,
                index = index,
                count = gridItems.size,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
