package com.thebluealliance.android.data.repository

import app.cash.turbine.test
import com.thebluealliance.android.data.local.dao.AllianceDao
import com.thebluealliance.android.data.local.dao.AwardDao
import com.thebluealliance.android.data.local.dao.EventDao
import com.thebluealliance.android.data.local.dao.EventTeamDao
import com.thebluealliance.android.data.local.dao.RankingDao
import com.thebluealliance.android.data.local.entity.EventEntity
import com.thebluealliance.android.data.remote.TbaApi
import com.thebluealliance.android.data.remote.dto.EventDto
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class EventRepositoryTest {

    private val api: TbaApi = mockk()
    private val eventDao: EventDao = mockk(relaxUnitFun = true)
    private val awardDao: AwardDao = mockk(relaxUnitFun = true)
    private val rankingDao: RankingDao = mockk(relaxUnitFun = true)
    private val allianceDao: AllianceDao = mockk(relaxUnitFun = true)
    private val eventTeamDao: EventTeamDao = mockk(relaxUnitFun = true)

    private val repo = EventRepository(api, eventDao, awardDao, rankingDao, allianceDao, eventTeamDao)

    @Test
    fun `refreshEventsForYear fetches from API and inserts into DAO`() = runTest {
        val dto = EventDto(
            key = "2024casj",
            name = "Silicon Valley Regional",
            eventCode = "casj",
            year = 2024,
            city = "San Jose",
            stateProv = "CA",
            country = "USA",
            startDate = "2024-03-27",
            endDate = "2024-03-30",
        )
        coEvery { api.getEventsForYear(2024) } returns listOf(dto)

        val insertedSlot = slot<List<EventEntity>>()
        coEvery { eventDao.insertAll(capture(insertedSlot)) } returns Unit

        repo.refreshEventsForYear(2024)

        coVerify(exactly = 1) { api.getEventsForYear(2024) }
        coVerify(exactly = 1) { eventDao.insertAll(any()) }

        val inserted = insertedSlot.captured
        assertEquals(1, inserted.size)
        assertEquals("2024casj", inserted[0].key)
        assertEquals("Silicon Valley Regional", inserted[0].name)
        assertEquals(2024, inserted[0].year)
        assertEquals("CA", inserted[0].state)
    }

    @Test
    fun `observeEventsForYear returns domain models from DAO`() = runTest {
        val entity = EventEntity(
            key = "2024casj",
            name = "Silicon Valley Regional",
            eventCode = "casj",
            year = 2024,
            type = null,
            district = null,
            city = "San Jose",
            state = "CA",
            country = "USA",
            startDate = "2024-03-27",
            endDate = "2024-03-30",
            week = 4,
            shortName = "Silicon Valley",
            website = null,
            timezone = null,
            webcasts = null,
            locationName = null,
            address = null,
            gmapsUrl = null,
        )
        every { eventDao.observeByYear(2024) } returns flowOf(listOf(entity))

        repo.observeEventsForYear(2024).test {
            val events = awaitItem()
            assertEquals(1, events.size)
            assertEquals("2024casj", events[0].key)
            assertEquals("Silicon Valley Regional", events[0].name)
            assertEquals("CA", events[0].state)
            awaitComplete()
        }
    }
}
