package com.thebluealliance.android.data.repository

import com.thebluealliance.android.data.local.TBADatabase
import com.thebluealliance.android.data.local.dao.DistrictDao
import com.thebluealliance.android.data.local.dao.DistrictRankingDao
import com.thebluealliance.android.data.remote.TbaApi
import com.thebluealliance.android.data.remote.dto.DistrictDto
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class DistrictRepositoryTest {

    private val api: TbaApi = mockk()
    private val db: TBADatabase = mockk(relaxed = true) {
        val executor = java.util.concurrent.Executors.newSingleThreadExecutor()
        every { queryExecutor } returns executor
        every { transactionExecutor } returns executor
    }
    private val districtDao: DistrictDao = mockk(relaxUnitFun = true)
    private val districtRankingDao: DistrictRankingDao = mockk(relaxUnitFun = true)

    private val repo = DistrictRepository(api, db, districtDao, districtRankingDao)

    @Test
    fun `getDistrictHistory returns years sorted descending`() = runTest {
        val dtos = listOf(
            DistrictDto(abbreviation = "ne", displayName = "New England", key = "2022ne", year = 2022),
            DistrictDto(abbreviation = "ne", displayName = "New England", key = "2024ne", year = 2024),
            DistrictDto(abbreviation = "ne", displayName = "New England", key = "2023ne", year = 2023),
        )
        coEvery { api.getDistrictHistory("ne") } returns dtos

        val years = repo.getDistrictHistory("ne")

        assertEquals(listOf(2024, 2023, 2022), years)
    }

    @Test
    fun `getDistrictHistory returns empty list when API returns empty`() = runTest {
        coEvery { api.getDistrictHistory("ne") } returns emptyList()

        val years = repo.getDistrictHistory("ne")

        assertEquals(emptyList<Int>(), years)
    }
}
