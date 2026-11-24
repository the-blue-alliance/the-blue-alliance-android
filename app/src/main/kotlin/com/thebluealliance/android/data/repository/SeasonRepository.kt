package com.thebluealliance.android.data.repository

import com.thebluealliance.android.data.remote.TbaApi
import com.thebluealliance.android.data.remote.dto.ApiStatusDto
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.time.Year
import javax.inject.Inject
import javax.inject.Singleton

/**
 * TBA's season bounds, fetched at most once per app session and shared by every year picker so
 * they all open on the same season. A failed fetch isn't cached; the next caller retries.
 */
@Singleton
class SeasonRepository
    @Inject
    constructor(
        private val api: TbaApi,
    ) {
        private val mutex = Mutex()
        private var status: ApiStatusDto? = null

        private suspend fun status(): ApiStatusDto? =
            mutex.withLock {
                status ?: try {
                    api.getStatus().also { status = it }
                } catch (e: CancellationException) {
                    throw e
                } catch (_: Exception) {
                    null
                }
            }

        /** The newest season a picker offers, or null when TBA's status is unavailable. */
        suspend fun maxSeason(): Int? = status()?.maxSeason

        /** The season pickers open on: TBA's current season, or the calendar year without a status. */
        suspend fun defaultSeason(): Int = status()?.currentSeason ?: Year.now().value
    }
