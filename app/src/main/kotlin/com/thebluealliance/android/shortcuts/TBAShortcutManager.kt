package com.thebluealliance.android.shortcuts

import android.R.attr.label
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.util.Log
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.core.net.toUri
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.thebluealliance.android.R
import com.thebluealliance.android.data.remote.ClientApi
import com.thebluealliance.android.data.remote.TbaApi
import com.thebluealliance.android.data.repository.EventRepository
import com.thebluealliance.android.data.repository.MyTBARepository
import com.thebluealliance.android.data.repository.TeamRepository
import com.thebluealliance.android.domain.model.Favorite
import com.thebluealliance.android.domain.model.Media
import com.thebluealliance.android.domain.model.ModelType
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Manages shortcuts for The Blue Alliance.
 *
 * We take teams and events that the user has favorited and make dynamic shortcuts from those favorites.
 *
 * See https://developer.android.com/develop/ui/views/launch/shortcuts
 */
class TBAShortcutManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val tbaRepository: MyTBARepository,
    private val eventRepository: EventRepository,
    private val teamRepository: TeamRepository,
    private val tbaApi: TbaApi,
) {

    /**
     * Start watching the favorites repository for changes, syncing favories to Android's shortcuts
     * system.
     */
    fun beginSyncingShortcuts() {
        ProcessLifecycleOwner.get().lifecycleScope.launch {
            tbaRepository.observeFavorites()
                .collectLatest { favorites ->
                    purgeUnfavoritedShortcuts(favorites)

                    val shortcuts = favorites
                        .take(ShortcutManagerCompat.getMaxShortcutCountPerActivity(context))
                        .mapNotNull { it.getShortcutInfo() }
                    ShortcutManagerCompat.addDynamicShortcuts(context, shortcuts)
                }
        }
    }

    /**
     * Remove any shortcuts in ShortcutManager that are no longer favorites
     */
    private fun purgeUnfavoritedShortcuts(favorites: List<Favorite>) {
        val shortcutIds = ShortcutManagerCompat.getDynamicShortcuts(context).map { it.id }
        val favoriteIds = favorites.map { it.modelKey }.toSet()
        val idsToRemove = shortcutIds - favoriteIds
        ShortcutManagerCompat.removeDynamicShortcuts(context, idsToRemove)
    }

    private suspend fun Favorite.getShortcutInfo(): ShortcutInfoCompat? {
        return when (modelType) {
            ModelType.TEAM -> getTeamShortcut(modelKey)
            ModelType.EVENT -> getEventShortcut(modelKey)
            else -> null
        }
    }

    private suspend fun getTeamShortcut(teamKey: String): ShortcutInfoCompat? {
        val currentSeason: Int = try {
            tbaApi.getStatus().currentSeason
        } catch(_: Exception) {
            Log.w("TBAShortcutManager", "Failed to fetch current season")
            return null
        }

        // Start by ensuring we have the latest team info in the repository
        // Particularly important for teams, as we don't start loading teams on app launch
        try {
            teamRepository.refreshTeam(teamKey)
            teamRepository.refreshTeamMedia(teamKey, currentSeason)
        } catch (_: Exception) {
            Log.w("TBAShortcutManager", "Failed to refresh team $teamKey")
        }

        val team = teamRepository.observeTeam(teamKey).first() ?: return null
        val media = teamRepository.observeTeamMedia(teamKey, currentSeason).first()

        val label = "${team.number} ${team.nickname}"
        val uri = "https://www.thebluealliance.com/team/${team.number}".toUri()
        val avatar = media.firstOrNull { it.isAvatar }

        return ShortcutInfoCompat.Builder(context, teamKey)
            .setShortLabel(label)
            .setIcon(getTeamShortcutIcon(avatar))
            .setIntent(generateIntent(uri))
            .build()
    }

    private fun getTeamShortcutIcon(avatar: Media?): IconCompat {
        if (avatar != null) {
            try {
                val bytes = Base64.decode(avatar.base64Image, Base64.DEFAULT)
                val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                return IconCompat.createWithBitmap(bitmap)
            } catch (_: Exception) {}
        }

        return IconCompat.createWithResource(context, R.drawable.ic_team_shortcut)
    }

    private suspend fun getEventShortcut(eventKey: String): ShortcutInfoCompat? {
        // Start by ensuring we have the latest event info in our repository
        try {
            eventRepository.refreshEvent(eventKey)
        } catch (_: Exception) {
            Log.w("TBAShortcutManager", "Failed to refresh event $eventKey")
        }

        val event = eventRepository.observeEvent(eventKey).first() ?: return null
        val label = "${event.year} ${event.name}"
        val uri = "https://www.thebluealliance.com/event/${event.key}".toUri()

        return ShortcutInfoCompat.Builder(context, eventKey)
            .setShortLabel(label)
            .setIcon(IconCompat.createWithResource(context, R.drawable.ic_event_shortcut))
            .setIntent(generateIntent(uri))
            .build()
    }

    private fun generateIntent(uri: Uri): Intent {
        return Intent(Intent.ACTION_VIEW, uri).apply {
            setClassName(context, "com.thebluealliance.android.LaunchShortcutActivity")
        }
    }
}
