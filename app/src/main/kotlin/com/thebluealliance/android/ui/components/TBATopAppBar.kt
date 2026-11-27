package com.thebluealliance.android.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalRippleConfiguration
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.thebluealliance.android.R
import com.thebluealliance.android.ui.theme.OnBrand
import com.thebluealliance.android.ui.theme.SurfaceFocusRing
import com.thebluealliance.android.ui.theme.TbaIcons
import com.thebluealliance.android.ui.theme.brandFocusRing

/**
 * A top-bar icon button that moves into the overflow menu, as a labelled item, when space is short.
 * [stateDescription] is announced in both places, for icons that show an on/off state.
 */
@Immutable
data class TopBarAction(
    val label: String,
    val icon: ImageVector,
    val onClick: () -> Unit,
    val stateDescription: String? = null,
)

private fun Modifier.actionState(action: TopBarAction): Modifier =
    action.stateDescription?.let { state -> semantics { stateDescription = state } } ?: this

/** Width of an icon button in the top bar, including its touch target. */
internal val TopBarActionWidth = 48.dp

/** Narrowest the title may get before secondary actions collapse into the overflow menu. */
internal val TopBarMinTitleWidth = 120.dp

// TopAppBar's own inset before the navigation icon and after the actions.
private val TopAppBarEdgePadding = 4.dp

/**
 * Whether [secondaryCount] secondary actions should collapse into the overflow menu: they leave
 * the title under [minTitleWidth] in a bar of [barWidth] with [fixedWidth] taken by everything
 * else, and collapsing them saves space. With [hasOverflowMenu] false, collapsing adds the menu.
 * [pinnedCount] buttons, such as Close, never collapse but take their width either way.
 */
internal fun topBarCollapsesActions(
    barWidth: Dp,
    fixedWidth: Dp,
    secondaryCount: Int,
    hasOverflowMenu: Boolean,
    pinnedCount: Int = 0,
    actionWidth: Dp = TopBarActionWidth,
    minTitleWidth: Dp = TopBarMinTitleWidth,
): Boolean {
    if (secondaryCount == 0) return false
    val collapsible = secondaryCount + if (hasOverflowMenu) 1 else 0
    val buttons = collapsible + pinnedCount
    return collapsible > 1 && barWidth - fixedWidth - actionWidth * buttons < minTitleWidth
}

/** The bar's trailing buttons. [MenuItem] is a lone menu item shown as an icon button. */
internal enum class TopBarSlot { Secondary, Overflow, MenuItem, Close }

/**
 * The bar's trailing buttons in order: secondary actions unless [collapse], then More options for
 * a menu of two or more items, or the lone item itself as an icon, then Close, which is always last
 * and never collapses. The menu holds the collapsed secondary actions and [overflowCount] items.
 */
internal fun topBarSlots(
    secondaryCount: Int,
    overflowCount: Int,
    hasClose: Boolean,
    collapse: Boolean,
): List<TopBarSlot> =
    buildList {
        if (!collapse) repeat(secondaryCount) { add(TopBarSlot.Secondary) }
        when ((if (collapse) secondaryCount else 0) + overflowCount) {
            0 -> Unit
            1 -> add(TopBarSlot.MenuItem)
            else -> add(TopBarSlot.Overflow)
        }
        if (hasClose) add(TopBarSlot.Close)
    }

/**
 * The app's top bar. Pass [navigationIcon] as null when there is none, so the bar's measurement
 * reserves no room for it. A plain-text [title] should be a [TopBarTitle], which makes it the
 * heading. A non-null [onRefresh] puts the Refresh accessibility action on the title, for screens
 * whose pull-to-refresh gesture has no semantic equivalent, and lets F5 and Ctrl+R refresh the
 * screen (see [RefreshTargets]); it does nothing while [isRefreshing].
 *
 * [secondaryActions] show as icon buttons, then a More options button opens a menu of
 * [overflowActions]. When the bar is too narrow to keep the title readable, the secondary actions
 * move to the top of that menu. A menu that would hold one item shows that item as an icon button
 * instead. [closeAction], when set, is the last button, after More options, and never collapses.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TBATopAppBar(
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    navigationIcon: (@Composable () -> Unit)? = null,
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
    isRefreshing: Boolean = false,
    secondaryActions: List<TopBarAction> = emptyList(),
    overflowActions: List<TopBarAction> = emptyList(),
    closeAction: TopBarAction? = null,
) {
    // Beside a rail the lamp is in the rail's header.
    val showLamp = showLamp && !LocalNavigationRailShown.current
    val hasNavigationIcon = showLamp || navigationIcon != null
    // The title's Refresh action ignores requests while busy, as pull-to-refresh does.
    val currentRefreshing by rememberUpdatedState(isRefreshing)
    val titleRefresh =
        onRefresh?.let { refresh ->
            remember(refresh) { { if (!currentRefreshing) refresh() } }
        }
    // F5 and Ctrl+R refresh the screen in front through the same guarded call.
    RefreshShortcutEffect(titleRefresh)
    // A brand bar (the default) draws its focus rings in brand colors; a bar on a surface, such as
    // the match pane's, keeps the theme's.
    val focusRing =
        if (colors.containerColor == MaterialTheme.colorScheme.primaryContainer) {
            brandFocusRing(colors.containerColor)
        } else {
            LocalRippleConfiguration.current
        }
    CompositionLocalProvider(LocalRippleConfiguration provides focusRing) {
        TBATopAppBarContent(
            title = title,
            modifier = modifier,
            showLamp = showLamp,
            navigationIcon = navigationIcon,
            hasNavigationIcon = hasNavigationIcon,
            titleRefresh = titleRefresh,
            windowInsets = windowInsets,
            colors = colors,
            scrollBehavior = scrollBehavior,
            secondaryActions = secondaryActions,
            overflowActions = overflowActions,
            closeAction = closeAction,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TBATopAppBarContent(
    title: @Composable () -> Unit,
    modifier: Modifier,
    showLamp: Boolean,
    navigationIcon: (@Composable () -> Unit)?,
    hasNavigationIcon: Boolean,
    titleRefresh: (() -> Unit)?,
    windowInsets: androidx.compose.foundation.layout.WindowInsets,
    colors: TopAppBarColors,
    scrollBehavior: TopAppBarScrollBehavior?,
    secondaryActions: List<TopBarAction>,
    overflowActions: List<TopBarAction>,
    closeAction: TopBarAction?,
) {
    TopAppBar(
        title = {
            // The title content owns its heading and Refresh action: TopBarTitle for plain text,
            // and interactive titles read LocalTopBarRefresh for their own focus node.
            CompositionLocalProvider(LocalTopBarRefresh provides titleRefresh, content = title)
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
                navigationIcon ?: {}
            },
        actions = {
            // The actions slot spans the bar less its end padding, so this measures the bar.
            BoxWithConstraints {
                val collapse =
                    topBarCollapsesActions(
                        barWidth = maxWidth + TopAppBarEdgePadding,
                        fixedWidth =
                            TopAppBarEdgePadding * 2 +
                                if (hasNavigationIcon) TopBarActionWidth else 0.dp,
                        secondaryCount = secondaryActions.size,
                        hasOverflowMenu = overflowActions.isNotEmpty(),
                        pinnedCount = if (closeAction != null) 1 else 0,
                    )
                val slots =
                    topBarSlots(
                        secondaryCount = secondaryActions.size,
                        overflowCount = overflowActions.size,
                        hasClose = closeAction != null,
                        collapse = collapse,
                    )
                val menuActions =
                    if (collapse) secondaryActions + overflowActions else overflowActions
                Row(verticalAlignment = Alignment.CenterVertically) {
                    var secondaryIndex = 0
                    slots.forEach { slot ->
                        when (slot) {
                            TopBarSlot.Secondary ->
                                TopBarIconButton(secondaryActions[secondaryIndex++])
                            TopBarSlot.Overflow -> TopBarOverflowMenu(menuActions)
                            TopBarSlot.MenuItem -> TopBarIconButton(menuActions.single())
                            TopBarSlot.Close -> closeAction?.let { TopBarIconButton(it) }
                        }
                    }
                }
            }
        },
        windowInsets = windowInsets,
        colors = colors,
        scrollBehavior = scrollBehavior,
    )
}

@Composable
private fun TopBarIconButton(action: TopBarAction) {
    IconButton(onClick = action.onClick, modifier = Modifier.actionState(action)) {
        Icon(action.icon, contentDescription = action.label)
    }
}

@Composable
private fun TopBarOverflowMenu(actions: List<TopBarAction>) {
    var expanded by remember { mutableStateOf(false) }
    val close = { expanded = false }
    Box {
        IconButton(onClick = { expanded = true }) {
            Icon(TbaIcons.MoreVert, contentDescription = "More options")
        }
        DropdownMenu(expanded = expanded, onDismissRequest = close) {
            SurfaceFocusRing {
                actions.forEach { action ->
                    DropdownMenuItem(
                        text = { Text(action.label) },
                        leadingIcon = { Icon(action.icon, contentDescription = null) },
                        modifier = Modifier.actionState(action),
                        onClick = {
                            close()
                            action.onClick()
                        },
                    )
                }
            }
        }
    }
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
