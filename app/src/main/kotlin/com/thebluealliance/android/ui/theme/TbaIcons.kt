package com.thebluealliance.android.ui.theme

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import com.thebluealliance.android.R

/**
 * The app's icons: Material Symbols (outlined family, weight 400, grade 0, 24dp) copied as vector
 * drawables into res/drawable, per Google's guidance to use Material Symbols instead of the
 * unmaintained material-icons library. "Filled" names are the fill=1 variant, for selected states
 * and the icons that were filled before.
 * https://developer.android.com/develop/ui/compose/graphics/images/material
 */
object TbaIcons {
    val AddToHomeScreen: ImageVector @Composable get() = icon(R.drawable.ic_add_to_home_screen)
    val ArrowBack: ImageVector @Composable get() = icon(R.drawable.ic_arrow_back)
    val ArrowDropDown: ImageVector @Composable get() = icon(R.drawable.ic_arrow_drop_down)
    val ArrowDropUp: ImageVector @Composable get() = icon(R.drawable.ic_arrow_drop_up)
    val Check: ImageVector @Composable get() = icon(R.drawable.ic_check)
    val Close: ImageVector @Composable get() = icon(R.drawable.ic_close)
    val Delete: ImageVector @Composable get() = icon(R.drawable.ic_delete_filled)
    val EmojiEvents: ImageVector @Composable get() = icon(R.drawable.ic_emoji_events_filled)
    val KeyboardArrowDown: ImageVector @Composable get() = icon(R.drawable.ic_keyboard_arrow_down)
    val KeyboardArrowRight: ImageVector @Composable get() = icon(R.drawable.ic_keyboard_arrow_right)
    val Language: ImageVector @Composable get() = icon(R.drawable.ic_language)
    val LocationOn: ImageVector @Composable get() = icon(R.drawable.ic_location_on)
    val MoreVert: ImageVector @Composable get() = icon(R.drawable.ic_more_vert)
    val Notifications: ImageVector @Composable get() = icon(R.drawable.ic_notifications_filled)
    val NotificationsOutlined: ImageVector @Composable get() = icon(R.drawable.ic_notifications)
    val Person: ImageVector @Composable get() = icon(R.drawable.ic_person_filled)
    val PlayCircle: ImageVector @Composable get() = icon(R.drawable.ic_play_circle)
    val Search: ImageVector @Composable get() = icon(R.drawable.ic_search)
    val Settings: ImageVector @Composable get() = icon(R.drawable.ic_settings_filled)
    val Share: ImageVector @Composable get() = icon(R.drawable.ic_share_filled)
    val Star: ImageVector @Composable get() = icon(R.drawable.ic_star_filled)
    val StarOutlined: ImageVector @Composable get() = icon(R.drawable.ic_star)
}

@Composable
private fun icon(
    @DrawableRes id: Int,
): ImageVector = ImageVector.vectorResource(id)
