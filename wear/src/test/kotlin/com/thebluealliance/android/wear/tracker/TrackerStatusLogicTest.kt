package com.thebluealliance.android.wear.tracker

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class TrackerStatusLogicTest {
    private val updatedAt = 1_700_000_000_000L

    private fun resolve(
        hasData: Boolean = false,
        lastSuccessAtMillis: Long? = null,
        lastRefreshFailed: Boolean = false,
        work: RefreshWork = RefreshWork.IDLE,
        isOnline: Boolean = true,
    ) = TrackerStatusLogic.resolve(hasData, lastSuccessAtMillis, lastRefreshFailed, work, isOnline)

    @Test
    fun `offline with nothing stored says offline`() {
        assertEquals(TrackerStatus.Offline, resolve(isOnline = false))
    }

    @Test
    fun `a refresh waiting for the network says offline instead of spinning`() {
        assertEquals(TrackerStatus.Offline, resolve(work = RefreshWork.PENDING, isOnline = false))
    }

    @Test
    fun `a failed refresh while offline says offline`() {
        assertEquals(
            TrackerStatus.Offline,
            resolve(lastRefreshFailed = true, work = RefreshWork.PENDING, isOnline = false),
        )
    }

    @Test
    fun `stored data carries when it was fetched`() {
        assertEquals(
            TrackerStatus.Content(updatedAtMillis = updatedAt, offline = false),
            resolve(hasData = true, lastSuccessAtMillis = updatedAt),
        )
    }

    @Test
    fun `stored data while offline is marked offline`() {
        assertEquals(
            TrackerStatus.Content(updatedAtMillis = updatedAt, offline = true),
            resolve(hasData = true, lastSuccessAtMillis = updatedAt, isOnline = false),
        )
    }

    @Test
    fun `stored data stays up while a refresh runs`() {
        assertEquals(
            TrackerStatus.Content(updatedAtMillis = updatedAt, offline = false),
            resolve(hasData = true, lastSuccessAtMillis = updatedAt, work = RefreshWork.RUNNING),
        )
    }

    @Test
    fun `a successful load with no events shows the empty content`() {
        assertEquals(
            TrackerStatus.Content(updatedAtMillis = updatedAt, offline = false),
            resolve(lastSuccessAtMillis = updatedAt),
        )
    }

    @Test
    fun `a successful load with no events stays up while offline`() {
        assertEquals(
            TrackerStatus.Content(updatedAtMillis = updatedAt, offline = true),
            resolve(lastSuccessAtMillis = updatedAt, work = RefreshWork.PENDING, isOnline = false),
        )
    }

    @Test
    fun `a failed refresh after a successful empty load keeps the empty content`() {
        assertEquals(
            TrackerStatus.Content(updatedAtMillis = updatedAt, offline = false),
            resolve(lastSuccessAtMillis = updatedAt, lastRefreshFailed = true),
        )
    }

    @Test
    fun `a running refresh with nothing stored shows a spinner`() {
        assertEquals(TrackerStatus.Loading, resolve(work = RefreshWork.RUNNING))
    }

    @Test
    fun `a queued refresh with nothing stored shows a spinner when online`() {
        assertEquals(TrackerStatus.Loading, resolve(work = RefreshWork.PENDING))
    }

    @Test
    fun `a failed refresh with nothing stored offers retry`() {
        assertEquals(TrackerStatus.LoadFailed, resolve(lastRefreshFailed = true))
    }
}
