package com.thebluealliance.android.wear.tracker

/** What the refresh worker is doing right now, read from WorkManager. */
internal enum class RefreshWork {
    /** A refresh is running. */
    RUNNING,

    /** A refresh is queued, for example waiting for its network constraint. */
    PENDING,

    /** Nothing is queued to run soon. */
    IDLE,
}

/** What the tracker screen shows under the team header. */
internal sealed interface TrackerStatus {
    /** Nothing to show yet; a refresh is under way. */
    data object Loading : TrackerStatus

    /** Nothing to show yet, and there's no network to fetch it. */
    data object Offline : TrackerStatus

    /** Nothing to show, and the last refresh failed. */
    data object LoadFailed : TrackerStatus

    /** Show what's stored, with when it was last fetched and whether the watch is offline. */
    data class Content(
        val updatedAtMillis: Long?,
        val offline: Boolean,
    ) : TrackerStatus
}

internal object TrackerStatusLogic {
    /**
     * @param hasData whether anything is stored to show (event, matches, or upcoming events)
     * @param lastSuccessAtMillis when a refresh last succeeded, or null if never
     */
    @Suppress("UNUSED_PARAMETER")
    fun resolve(
        hasData: Boolean,
        lastSuccessAtMillis: Long?,
        lastRefreshFailed: Boolean,
        work: RefreshWork,
        isOnline: Boolean,
    ): TrackerStatus =
        when {
            // The screen shows a spinner from the moment a refresh is queued until the worker
            // finishes, and has no notion of being offline or of when data was fetched.
            work != RefreshWork.IDLE && !hasData -> TrackerStatus.Loading
            lastRefreshFailed && !hasData -> TrackerStatus.LoadFailed
            else -> TrackerStatus.Content(updatedAtMillis = null, offline = false)
        }
}
