package com.thebluealliance.android.shortcuts

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Looper
import android.util.Base64
import android.util.Log
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.core.net.toUri
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.thebluealliance.android.R
import com.thebluealliance.android.data.repository.EventRepository
import com.thebluealliance.android.data.repository.MyTBARepository
import com.thebluealliance.android.data.repository.SeasonRepository
import com.thebluealliance.android.data.repository.TeamRepository
import com.thebluealliance.android.domain.model.Favorite
import com.thebluealliance.android.domain.model.Media
import com.thebluealliance.android.domain.model.ModelType
import com.thebluealliance.android.domain.teamLabel
import com.thebluealliance.android.ui.common.UserMessageBus
import com.thebluealliance.android.util.addRoundedCorners
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages shortcuts for The Blue Alliance.
 *
 * We take teams and events that the user has favorited and make dynamic shortcuts from those favorites.
 *
 * See https://developer.android.com/develop/ui/views/launch/shortcuts
 */
@Singleton
class TBAShortcutManager
    @Inject
    constructor(
        @param:ApplicationContext private val context: Context,
        private val tbaRepository: MyTBARepository,
        private val eventRepository: EventRepository,
        private val teamRepository: TeamRepository,
        private val seasons: SeasonRepository,
        private val userMessages: UserMessageBus,
    ) {
        private val attemptedMediaFetches = ConcurrentHashMap.newKeySet<Pair<String, Int>>()

        /**
         * Start watching the favorites repository for changes, syncing favorites to Android's shortcuts
         * system. Waits for the main thread's first idle so it does not compete with startup.
         */
        fun beginSyncingShortcuts() {
            Looper.getMainLooper().queue.addIdleHandler {
                startSyncing()
                false
            }
        }

        private fun startSyncing() {
            ProcessLifecycleOwner.get().lifecycleScope.launch {
                tbaRepository
                    .observeFavorites()
                    .collectLatest { favorites ->
                        withContext(Dispatchers.IO) {
                            purgeUnfavoritedShortcuts(favorites)
                            val season = seasons.defaultSeason()

                            val shortcuts =
                                favorites
                                    .take(
                                        ShortcutManagerCompat.getMaxShortcutCountPerActivity(
                                            context,
                                        ),
                                    ).mapNotNull { it.getShortcutInfo(season) }
                            ShortcutManagerCompat.setDynamicShortcuts(context, shortcuts)
                        }
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

        private suspend fun Favorite.getShortcutInfo(season: Int): ShortcutInfoCompat? =
            when (modelType) {
                ModelType.TEAM -> getTeamShortcut(modelKey, season)
                ModelType.EVENT -> getEventShortcut(modelKey)
                else -> null
            }

        private suspend fun getTeamShortcut(
            teamKey: String,
            season: Int,
        ): ShortcutInfoCompat? {
            val (team, avatar) =
                loadShortcutTeam(teamKey, teamRepository, season, attemptedMediaFetches)
                    ?: return null
            val label = teamLabel(team.number, team.nickname, team.name)
            val uri = "https://www.thebluealliance.com/team/${team.number}".toUri()

            return ShortcutInfoCompat
                .Builder(context, teamKey)
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
                    val density = context.resources.displayMetrics.density
                    val roundedBitmap = bitmap.addRoundedCorners(4f * density)
                    return IconCompat.createWithBitmap(roundedBitmap)
                } catch (e: Exception) {
                    Log.w("TBAShortcutManager", "Failed to decode avatar, using default icon", e)
                }
            }

            return IconCompat.createWithResource(context, R.drawable.ic_team_shortcut)
        }

        private suspend fun getEventShortcut(eventKey: String): ShortcutInfoCompat? {
            val event = loadShortcutEvent(eventKey, eventRepository) ?: return null
            val label = "${event.year} ${event.name}"
            val uri = "https://www.thebluealliance.com/event/${event.key}".toUri()

            return ShortcutInfoCompat
                .Builder(context, eventKey)
                .setShortLabel(label)
                .setIcon(IconCompat.createWithResource(context, R.drawable.ic_event_shortcut))
                .setIntent(generateIntent(uri))
                .build()
        }

        private fun generateIntent(uri: Uri): Intent =
            Intent(Intent.ACTION_VIEW, uri).apply {
                setClassName(context, "com.thebluealliance.android.LaunchShortcutActivity")
            }

        /**
         * Returns whether the device supports pinning shortcuts to the home screen.
         */
        fun canPinShortcuts(): Boolean =
            ShortcutManagerCompat.isRequestPinShortcutSupported(context)

        /**
         * Request to pin a shortcut for the given favorite to the user's home screen.
         * This will prompt the user with the system's pinned shortcut dialog.
         */
        suspend fun requestPinShortcut(favorite: Favorite) {
            val shortcutInfo = favorite.getShortcutInfo(seasons.defaultSeason())
            if (shortcutInfo == null) {
                userMessages.show("Failed to create shortcut")
                return
            }

            try {
                val success = ShortcutManagerCompat.requestPinShortcut(context, shortcutInfo, null)
                if (!success) {
                    userMessages.show("Failed to add shortcut to home screen")
                }
            } catch (e: IllegalStateException) {
                // requestPinShortcut throws if the app is no longer in the foreground
                // (e.g. user switched away during the network fetch for shortcut info)
                Log.w("TBAShortcutManager", "Cannot pin shortcut while in background", e)
            }
        }
    }
