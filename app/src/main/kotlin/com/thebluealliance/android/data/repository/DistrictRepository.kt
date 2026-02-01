package com.thebluealliance.android.data.repository

import com.thebluealliance.android.data.local.dao.DistrictDao
import com.thebluealliance.android.data.local.dao.DistrictRankingDao
import com.thebluealliance.android.data.mappers.toDomain
import com.thebluealliance.android.data.mappers.toEntity
import com.thebluealliance.android.data.remote.TbaApi
import com.thebluealliance.android.domain.model.District
import com.thebluealliance.android.domain.model.DistrictRanking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DistrictRepository @Inject constructor(
    private val api: TbaApi,
    private val districtDao: DistrictDao,
    private val districtRankingDao: DistrictRankingDao,
) {
    fun observeDistrict(key: String): Flow<District?> =
        districtDao.observe(key).map { it?.toDomain() }

    fun observeDistrictsForYear(year: Int): Flow<List<District>> =
        districtDao.observeByYear(year).map { list -> list.map { it.toDomain() } }

    fun observeDistrictRankings(districtKey: String): Flow<List<DistrictRanking>> =
        districtRankingDao.observeByDistrict(districtKey).map { list -> list.map { it.toDomain() } }

    suspend fun refreshDistrictsForYear(year: Int) {
        val dtos = api.getDistrictsForYear(year)
        districtDao.insertAll(dtos.map { it.toEntity() })
    }

    suspend fun refreshDistrictRankings(districtKey: String) {
        val dtos = api.getDistrictRankings(districtKey) ?: return
        districtRankingDao.deleteByDistrict(districtKey)
        districtRankingDao.insertAll(dtos.map { it.toEntity(districtKey) })
    }
}
