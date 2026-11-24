package com.thebluealliance.android.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.thebluealliance.android.R
import com.thebluealliance.android.ui.theme.OnBrand

/**
 * The app's top bar. A plain-text [title] should be a [TopBarTitle], which makes it the heading. A
 * non-null [onRefresh] puts the Refresh accessibility action on the title, for screens whose
 * pull-to-refresh gesture has no semantic equivalent.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TBATopAppBar(
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    windowInsets: androidx.compose.foundation.layout.WindowInsets = TopAppBarDefaults.windowInsets,
    colors: TopAppBarColors =
        TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = OnBrand,
            navigationIconContentColor = OnBrand,
            actionIconContentColor = OnBrand,
        ),
    scrollBehavior: TopAppBarScrollBehavior? = null,
    showLamp: Boolean = false,
    onRefresh: (() -> Unit)? = null,
) {
    TopAppBar(
        title = {
            // The title content owns its heading and Refresh action: TopBarTitle for plain text,
            // and interactive titles read LocalTopBarRefresh for their own focus node.
            CompositionLocalProvider(LocalTopBarRefresh provides onRefresh, content = title)
        },
        modifier = modifier,
        navigationIcon =
            if (showLamp) {
                {
                    // 48dp matches the back button's IconButton width in detail views, so title
                    // text shares a consistent left edge across all screens. A plain Box keeps
                    // the decorative lamp out of the semantics tree.
                    Box(modifier = Modifier.size(48.dp), contentAlignment = Alignment.Center) {
                        Icon(
                            painter = painterResource(id = R.drawable.tba_lamp),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = OnBrand,
                        )
                    }
                }
            } else {
                navigationIcon
            },
        actions = actions,
        windowInsets = windowInsets,
        colors = colors,
        scrollBehavior = scrollBehavior,
    )
}

/**
 * A plain-text top-bar title: the screen's heading, carrying the bar's Refresh accessibility action
 * when it has one.
 */
@Composable
fun TopBarTitle(
    text: String,
    modifier: Modifier = Modifier,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip,
) {
    Text(
        text = text,
        modifier = modifier.topBarTitleSemantics(LocalTopBarRefresh.current),
        maxLines = maxLines,
        overflow = overflow,
    )
}

/** Marks the node that owns a top-bar title: the heading, with the bar's [onRefresh] action. */
internal fun Modifier.topBarTitleSemantics(onRefresh: (() -> Unit)?): Modifier =
    semantics { heading() }.refreshAction(onRefresh)
