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
    fun resolve(
        hasData: Boolean,
        lastSuccessAtMillis: Long?,
        lastRefreshFailed: Boolean,
        work: RefreshWork,
        isOnline: Boolean,
    ): TrackerStatus =
        when {
            // Anything stored keeps showing, stamped with its age, whatever the refresh is doing.
            hasData -> TrackerStatus.Content(lastSuccessAtMillis, offline = !isOnline)
            work == RefreshWork.RUNNING -> TrackerStatus.Loading
            // A queued refresh can't run without a network, so say so instead of spinning.
            !isOnline -> TrackerStatus.Offline
            lastRefreshFailed -> TrackerStatus.LoadFailed
            work == RefreshWork.PENDING -> TrackerStatus.Loading
            // A refresh succeeded and found nothing: that's a real "no upcoming events".
            lastSuccessAtMillis != null ->
                TrackerStatus.Content(
                    lastSuccessAtMillis,
                    offline = false,
                )
            // Never loaded and nothing queued: the screen enqueues a refresh when it opens.
            else -> TrackerStatus.Loading
        }
}
