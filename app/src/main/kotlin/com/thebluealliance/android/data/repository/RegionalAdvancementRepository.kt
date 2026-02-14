package com.thebluealliance.android.data.repository

import androidx.room.withTransaction
import com.thebluealliance.android.data.local.TBADatabase
import com.thebluealliance.android.data.local.dao.RegionalRankingDao
import com.thebluealliance.android.data.mappers.toDomain
import com.thebluealliance.android.data.mappers.toEntity
import com.thebluealliance.android.data.remote.TbaApi
import com.thebluealliance.android.domain.model.RegionalRanking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RegionalAdvancementRepository @Inject constructor(
    private val api: TbaApi,
    private val db: TBADatabase,
    private val regionalRankingDao: RegionalRankingDao,
) {
    fun observeRegionalRankings(year: Int): Flow<List<RegionalRanking>> =
        regionalRankingDao.observeByYear(year).map { list -> list.map { it.toDomain() } }

    suspend fun refreshRegionalRankings(year: Int) {
        val dtos = api.getRegionalRankings(year)
        db.withTransaction {
            regionalRankingDao.deleteByYear(year)
            regionalRankingDao.insertAll(dtos.map { it.toEntity(year) })
        }
    }
}
