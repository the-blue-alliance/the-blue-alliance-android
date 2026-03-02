package com.thebluealliance.android.data.sync

import android.util.Log
import com.thebluealliance.android.data.local.dao.EventDao
import com.thebluealliance.android.data.local.dao.TeamDao
import com.thebluealliance.android.data.remote.TbaApi
import com.thebluealliance.android.data.repository.EventRepository
import com.thebluealliance.android.data.repository.TeamRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataSyncManager @Inject constructor(
    private val api: TbaApi,
    private val eventDao: EventDao,
    private val teamDao: TeamDao,
    private val eventRepository: EventRepository,
    private val teamRepository: TeamRepository,
) {
    companion object {
        private const val TAG = "DataSyncManager"
        private const val FIRST_EVENT_YEAR = 1992
        private const val TEAM_COUNT_THRESHOLD = 500
        private const val TEAM_PAGES = 20
    }

    suspend fun syncIfNeeded() {
        try {
            val status = api.getStatus()
            syncEvents(status.maxSeason)
            syncTeams()
        } catch (e: Exception) {
            Log.e(TAG, "Sync failed to get API status", e)
        }
    }

    private suspend fun syncEvents(maxSeason: Int) {
        val loadedYears = eventDao.getLoadedYears().toSet()
        val allYears = FIRST_EVENT_YEAR..maxSeason
        val missingYears = allYears.filter { it !in loadedYears }

        if (missingYears.isEmpty()) {
            Log.d(TAG, "All event years already loaded")
            return
        }

        Log.d(TAG, "Syncing ${missingYears.size} missing event years")
        for (year in missingYears) {
            try {
                eventRepository.refreshEventsForYear(year)
                Log.d(TAG, "Synced events for $year")
            } catch (e: Exception) {
                Log.w(TAG, "Failed to sync events for $year", e)
            }
        }
    }

    private suspend fun syncTeams() {
        val teamCount = teamDao.getCount()
        if (teamCount >= TEAM_COUNT_THRESHOLD) {
            Log.d(TAG, "Teams already loaded ($teamCount)")
            return
        }

        Log.d(TAG, "Syncing teams ($teamCount below threshold)")
        for (page in 0 until TEAM_PAGES) {
            try {
                teamRepository.refreshTeamsPage(page)
                Log.d(TAG, "Synced teams page $page")
            } catch (e: Exception) {
                Log.w(TAG, "Failed to sync teams page $page", e)
            }
        }
    }
}
