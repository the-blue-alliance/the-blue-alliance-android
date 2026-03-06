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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PlayCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.thebluealliance.android.domain.model.Media
import com.thebluealliance.android.ui.common.EmptyBox
import com.thebluealliance.android.ui.common.LoadingBox
import com.thebluealliance.android.util.openUrl

/**
 * Data class for a media item to display in a media grid.
 */
data class MediaGridItem(
    val type: String,
    val foreignKey: String,
)

/**
 * Returns a thumbnail URL for a media item, or null if the type isn't supported.
 */
fun mediaUrl(type: String, foreignKey: String): String? = when (type) {
    "imgur" -> "https://i.imgur.com/${foreignKey}.png"
    "cdphotothread" -> "https://www.chiefdelphi.com/media/img/${foreignKey}"
    "instagram-image" -> "https://www.instagram.com/p/${foreignKey}/media/"
    "youtube" -> "https://img.youtube.com/vi/${foreignKey}/hqdefault.jpg"
    "grabcad" -> null
    "onshape" -> null
    else -> null
}

/**
 * Returns the external link URL for a media item, or null if the type isn't supported.
 */
fun mediaLinkUrl(type: String, foreignKey: String): String? = when (type) {
    "imgur" -> "https://imgur.com/${foreignKey}"
    "cdphotothread" -> "https://www.chiefdelphi.com/media/img/${foreignKey}"
    "instagram-image" -> "https://www.instagram.com/p/${foreignKey}/"
    "youtube" -> "https://www.youtube.com/watch?v=${foreignKey}"
    else -> null
}

/**
 * A single media item with thumbnail, optional play overlay for YouTube, and click-to-open.
 */
@Composable
fun MediaItem(
    type: String,
    foreignKey: String,
    modifier: Modifier = Modifier,
) {
    val url = mediaUrl(type, foreignKey) ?: return
    val linkUrl = mediaLinkUrl(type, foreignKey)
    val context = LocalContext.current

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(MaterialTheme.shapes.medium)
            .then(
                if (linkUrl != null) {
                    Modifier.clickable {
                        context.openUrl(linkUrl)
                    }
                } else {
                    Modifier
                }
            ),
    ) {
        AsyncImage(
            model = url,
            contentDescription = "Media",
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize(),
        )
        if (type == "youtube") {
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(48.dp)
                    .background(Color.Black.copy(alpha = 0.5f), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    Icons.Outlined.PlayCircle,
                    contentDescription = "Play video",
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
    items.chunked(2).forEach { rowItems ->
        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            rowItems.forEach { item ->
                MediaItem(
                    type = item.type,
                    foreignKey = item.foreignKey,
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
 * A full-screen tab that displays a team's media in a 2-column grid.
 * Shared between Team detail and Team@Event detail screens.
 */
@Composable
fun MediaTab(
    media: List<Media>?,
    innerPadding: PaddingValues = PaddingValues.Zero,
) {
    if (media == null) {
        LoadingBox(modifier = Modifier.padding(innerPadding))
        return
    }
    val filtered = media.filter { it.type != "avatar" }
    if (filtered.isEmpty()) {
        EmptyBox(
            modifier = Modifier.padding(innerPadding),
            message = "No media"
        )
        return
    }
    val gridItems = filtered.mapNotNull { item ->
        if (mediaUrl(item.type, item.foreignKey) != null) {
            MediaGridItem(type = item.type, foreignKey = item.foreignKey)
        } else null
    }
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = innerPadding,
    ) {
        items(gridItems, key = { "${it.type}_${it.foreignKey}" }) { item ->
            MediaItem(
                type = item.type,
                foreignKey = item.foreignKey,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
