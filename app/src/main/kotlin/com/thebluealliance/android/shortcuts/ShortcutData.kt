package com.thebluealliance.android.shortcuts

import android.util.Log
import com.thebluealliance.android.data.repository.EventRepository
import com.thebluealliance.android.data.repository.TeamRepository
import com.thebluealliance.android.domain.model.Event
import com.thebluealliance.android.domain.model.Media
import com.thebluealliance.android.domain.model.Team
import kotlinx.coroutines.flow.first
import kotlin.coroutines.cancellation.CancellationException

private const val TAG = "TBAShortcutManager"

/**
 * The cached team and its avatar for [season], fetching only what the cache lacks. A team's
 * name and avatar rarely change, and its own screen refreshes them when opened. Falls back to
 * last season's cached avatar while [season] has none. [attemptedMediaFetches] holds the
 * (team, season) media fetches already tried this session, so a team without an avatar yet
 * is fetched once per session rather than on every sync.
 */
internal suspend fun loadShortcutTeam(
    teamKey: String,
    teamRepository: TeamRepository,
    season: Int,
    attemptedMediaFetches: MutableSet<Pair<String, Int>>,
): Pair<Team, Media?>? {
    if (teamRepository.observeTeam(teamKey).first() == null) {
        refreshQuietly(teamKey) { teamRepository.refreshTeam(teamKey) }
    }

    suspend fun cachedAvatar(year: Int) =
        teamRepository.observeTeamMedia(teamKey, year).first().firstOrNull { it.isAvatar }
    var avatar = cachedAvatar(season)
    if (avatar == null) {
        val fetch = teamKey to season
        if (attemptedMediaFetches.add(fetch)) {
            try {
                refreshQuietly(teamKey) { teamRepository.refreshTeamMedia(teamKey, season) }
            } catch (e: CancellationException) {
                attemptedMediaFetches.remove(fetch)
                throw e
            }
            avatar = cachedAvatar(season)
        }
        avatar = avatar ?: cachedAvatar(season - 1)
    }
    val team = teamRepository.observeTeam(teamKey).first() ?: return null
    return team to avatar
}

/** The cached event, fetched only if it is not cached yet. */
internal suspend fun loadShortcutEvent(
    eventKey: String,
    eventRepository: EventRepository,
): Event? {
    eventRepository.observeEvent(eventKey).first()?.let { return it }
    refreshQuietly(eventKey) { eventRepository.refreshEvent(eventKey) }
    return eventRepository.observeEvent(eventKey).first()
}

private suspend fun refreshQuietly(
    key: String,
    refresh: suspend () -> Unit,
) {
    try {
        refresh()
    } catch (e: CancellationException) {
        throw e
    } catch (_: Exception) {
        Log.w(TAG, "Failed to refresh $key")
    }
}
