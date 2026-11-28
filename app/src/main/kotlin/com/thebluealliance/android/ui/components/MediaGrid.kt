package com.thebluealliance.android.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.isSpecified
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Density
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
 *
 * [large] tiles, one or two to a row, ask for a bigger variant where the source has one: imgur's
 * 1024px "h", Instagram's 1080px "l" and YouTube's 1280px maxresdefault. YouTube lacks
 * maxresdefault for some videos, so callers fall back to the small URL when it fails.
 */
fun mediaUrl(
    type: String,
    foreignKey: String,
    large: Boolean = false,
): String? =
    when (type) {
        "imgur" -> "https://i.imgur.com/$foreignKey${if (large) "h" else "l"}.jpg"
        "cdphotothread" -> "https://www.chiefdelphi.com/media/img/$foreignKey"
        "instagram-image" ->
            "https://www.instagram.com/p/$foreignKey/media/${if (large) "?size=l" else ""}"
        "youtube" ->
            "https://img.youtube.com/vi/$foreignKey/${if (large) "maxresdefault" else "hqdefault"}.jpg"
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
 * Grid columns for [count] media tiles when [columnsThatFit] fit: a lone tile spans the column, two
 * split it in half, and three or more fill the columns that fit.
 */
internal fun mediaColumns(
    count: Int,
    columnsThatFit: Int,
): Int {
    val fit = columnsThatFit.coerceAtLeast(1)
    return if (count >= 3) fit else count.coerceIn(1, fit)
}

/** [GridCells.Adaptive] of [minSize], but never more columns than [mediaColumns] gives [count]. */
internal data class MediaGridCells(
    private val minSize: Dp,
    private val count: Int,
) : GridCells {
    override fun Density.calculateCrossAxisCellSizes(
        availableSize: Int,
        spacing: Int,
    ): List<Int> {
        val fit = (availableSize + spacing) / (minSize.roundToPx() + spacing)
        val columns = mediaColumns(count, fit)
        val gridSize = availableSize - spacing * (columns - 1)
        val slot = gridSize / columns
        val remaining = gridSize % columns
        return List(columns) { slot + if (it < remaining) 1 else 0 }
    }
}

/** Shape of a lone photo's tile until the photo loads and gives its own. */
private const val LONE_PHOTO_PLACEHOLDER_RATIO = 4f / 3f

/** A lone photo is no taller than this share of the visible grid, so its tile stays on screen. */
private const val LONE_MEDIA_MAX_HEIGHT_FRACTION = 0.6f

/**
 * The tallest a lone tile may be: [LONE_MEDIA_MAX_HEIGHT_FRACTION] of the grid's visible height,
 * which is [gridHeight] less its [verticalContentPadding] (the bars it scrolls under).
 */
internal fun loneMediaMaxHeight(
    gridHeight: Dp,
    verticalContentPadding: Dp,
): Dp = (gridHeight - verticalContentPadding).coerceAtLeast(0.dp) * LONE_MEDIA_MAX_HEIGHT_FRACTION

/**
 * A single media item with thumbnail, optional play overlay for YouTube, and click-to-open.
 *
 * [large] loads [mediaUrl]'s bigger variant, falling back to the small one if it fails.
 * [naturalAspect] shows the whole photo at its own shape instead of cropping it to [aspectRatio].
 */
@Composable
fun MediaItem(
    type: String,
    foreignKey: String,
    index: Int,
    count: Int,
    modifier: Modifier = Modifier,
    aspectRatio: Float = 1f,
    large: Boolean = false,
    naturalAspect: Boolean = false,
) {
    val smallUrl = mediaUrl(type, foreignKey) ?: return
    val largeUrl = if (large) mediaUrl(type, foreignKey, large = true) else null
    // Saved so a missing large thumbnail is not re-fetched when the tile comes back.
    var largeFailed by rememberSaveable(largeUrl) { mutableStateOf(false) }
    val url = largeUrl?.takeUnless { largeFailed } ?: smallUrl
    // Saved so the tile comes back at the photo's shape rather than the placeholder's.
    var photoRatio by rememberSaveable(type, foreignKey) { mutableStateOf<Float?>(null) }
    val ratio = if (naturalAspect) photoRatio ?: LONE_PHOTO_PLACEHOLDER_RATIO else aspectRatio
    val linkUrl = mediaLinkUrl(type, foreignKey)
    val context = LocalContext.current

    Box(
        modifier =
            modifier
                .aspectRatio(ratio)
                .clip(MaterialTheme.shapes.medium)
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .then(
                    if (linkUrl != null) {
                        Modifier.handCursor().clickable(
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
            contentScale = if (naturalAspect) ContentScale.Fit else ContentScale.Crop,
            modifier = Modifier.matchParentSize(),
            onSuccess = { state ->
                val size = state.painter.intrinsicSize
                if (naturalAspect && size.isSpecified && size.width > 0f && size.height > 0f) {
                    photoRatio = size.width / size.height
                }
            },
            onError = { if (url == largeUrl) largeFailed = true },
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
 * Renders media items in a grid of [columns] tiles of [aspectRatio] using Rows.
 * Designed to be called inside a LazyColumn item{} block (not as a nested lazy layout).
 */
@Composable
fun MediaGridRow(
    items: List<MediaGridItem>,
    modifier: Modifier = Modifier,
    columns: Int = 2,
    aspectRatio: Float = 1f,
) {
    items.chunked(columns).forEachIndexed { rowIndex, rowItems ->
        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            rowItems.forEachIndexed { column, item ->
                MediaItem(
                    type = item.type,
                    foreignKey = item.foreignKey,
                    index = rowIndex * columns + column,
                    count = items.size,
                    modifier = Modifier.weight(1f),
                    aspectRatio = aspectRatio,
                    large = items.size <= 2,
                )
            }
            repeat(columns - rowItems.size) {
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
 * A full-screen tab that displays a team's media in an adaptive grid sized by [mediaTileMinSize],
 * with no more columns than [mediaColumns] gives. A lone photo shows whole, at its own shape, no
 * taller than [loneMediaMaxHeight] allows. Shared between Team detail and
 * Team@Event detail screens.
 */
@Composable
fun MediaTab(
    media: List<Media>?,
    innerPadding: PaddingValues = PaddingValues.Zero,
    /** Attached to the first tile, so focus can be sent to the photos once they load. */
    firstItemFocus: FocusRequester? = null,
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
    val lone = gridItems.size == 1
    ReadingWidthBox(innerPadding, Modifier.fillMaxSize()) { readingPadding ->
        val scrollbarState = rememberLazyGridState()
        BoxWithConstraints(
            Modifier
                .fillMaxSize()
                .verticalScrollbar(scrollbarState, readingPadding)
                .padding(8.dp),
        ) {
            val loneMaxHeight =
                loneMediaMaxHeight(
                    gridHeight = maxHeight,
                    verticalContentPadding =
                        readingPadding.calculateTopPadding() +
                            readingPadding.calculateBottomPadding(),
                )
            LazyVerticalGrid(
                state = scrollbarState,
                columns =
                    MediaGridCells(
                        minSize = mediaTileMinSize(currentWindowAdaptiveInfo().windowSizeClass),
                        count = gridItems.size,
                    ),
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = readingPadding,
            ) {
                itemsIndexed(gridItems, key = {
                    _,
                    it,
                    ->
                    "${it.type}_${it.foreignKey}"
                }) { index, item ->
                    val focus =
                        if (index == 0 && firstItemFocus != null) {
                            Modifier.focusRequester(firstItemFocus)
                        } else {
                            Modifier
                        }
                    if (lone) {
                        // A capped tile narrows to keep its shape, centred in the column.
                        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
                            MediaItem(
                                type = item.type,
                                foreignKey = item.foreignKey,
                                index = index,
                                count = gridItems.size,
                                modifier = focus.heightIn(max = loneMaxHeight),
                                aspectRatio = 16f / 9f,
                                large = true,
                                naturalAspect = item.type != "youtube",
                            )
                        }
                    } else {
                        MediaItem(
                            type = item.type,
                            foreignKey = item.foreignKey,
                            index = index,
                            count = gridItems.size,
                            modifier = focus.fillMaxWidth(),
                            large = gridItems.size <= 2,
                        )
                    }
                }
            }
        }
    }
}
