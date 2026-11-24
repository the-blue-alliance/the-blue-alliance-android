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

/** Runs [fetch] on the first [get] only, so one sync shares a single result; null on failure. */
internal class FetchOnce<T : Any>(
    private val fetch: suspend () -> T,
) {
    private var fetched = false
    private var value: T? = null

    suspend fun get(): T? {
        if (!fetched) {
            fetched = true
            value =
                try {
                    fetch()
                } catch (e: CancellationException) {
                    throw e
                } catch (_: Exception) {
                    null
                }
        }
        return value
    }
}

/**
 * The cached team and its avatar for [season], fetching only what the cache lacks. A team's
 * name and avatar rarely change, and its own screen refreshes them when opened.
 */
internal suspend fun loadShortcutTeam(
    teamKey: String,
    teamRepository: TeamRepository,
    season: FetchOnce<Int>,
): Pair<Team, Media?>? {
    val year =
        season.get() ?: run {
            Log.w(TAG, "Failed to fetch current season")
            return null
        }
    if (teamRepository.observeTeam(teamKey).first() == null) {
        refreshQuietly(teamKey) { teamRepository.refreshTeam(teamKey) }
    }
    var avatar = teamRepository.observeTeamMedia(teamKey, year).first().firstOrNull { it.isAvatar }
    if (avatar == null) {
        refreshQuietly(teamKey) { teamRepository.refreshTeamMedia(teamKey, year) }
        avatar = teamRepository.observeTeamMedia(teamKey, year).first().firstOrNull { it.isAvatar }
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
