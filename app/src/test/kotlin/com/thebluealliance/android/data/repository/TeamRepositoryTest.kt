package com.thebluealliance.android.data.repository

import com.thebluealliance.android.data.local.TBADatabase
import com.thebluealliance.android.data.local.dao.EventTeamDao
import com.thebluealliance.android.data.local.dao.MediaDao
import com.thebluealliance.android.data.local.dao.TeamDao
import com.thebluealliance.android.data.local.dao.TeamEventStatusDao
import com.thebluealliance.android.data.remote.TbaApi
import com.thebluealliance.android.data.remote.dto.TeamDto
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class TeamRepositoryTest {
    private val api: TbaApi = mockk()
    private val db: TBADatabase = mockk(relaxed = true)
    private val teamDao: TeamDao = mockk(relaxUnitFun = true)
    private val mediaDao: MediaDao = mockk(relaxUnitFun = true)
    private val eventTeamDao: EventTeamDao = mockk(relaxUnitFun = true)
    private val teamEventStatusDao: TeamEventStatusDao = mockk(relaxUnitFun = true)

    private val repo = TeamRepository(api, db, teamDao, mediaDao, eventTeamDao, teamEventStatusDao)

    private fun teams(count: Int): List<TeamDto> =
        List(count) { TeamDto(key = "frc$it", teamNumber = it) }

    @Test
    fun `refreshAllTeams pages until the API returns an empty page`() =
        runTest {
            coEvery { api.getTeams(0) } returns teams(500)
            coEvery { api.getTeams(1) } returns teams(500)
            coEvery { api.getTeams(2) } returns teams(7)
            coEvery { api.getTeams(3) } returns emptyList()

            repo.refreshAllTeams()

            coVerify(exactly = 1) { api.getTeams(3) }
            coVerify(exactly = 0) { api.getTeams(4) }
            coVerify(exactly = 4) { teamDao.insertAll(any()) }
        }
}
