package com.thebluealliance.android.data.repository

import androidx.room.withTransaction
import com.thebluealliance.android.data.local.TBADatabase
import com.thebluealliance.android.data.local.dao.MatchDao
import com.thebluealliance.android.data.mappers.toDomain
import com.thebluealliance.android.data.mappers.toEntity
import com.thebluealliance.android.data.remote.TbaApi
import com.thebluealliance.android.domain.model.Match
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MatchRepository @Inject constructor(
    private val api: TbaApi,
    private val db: TBADatabase,
    private val matchDao: MatchDao,
) {
    fun observeEventMatches(eventKey: String): Flow<List<Match>> =
        matchDao.observeByEvent(eventKey).map { list -> list.map { it.toDomain() } }

    fun observeMatch(key: String): Flow<Match?> =
        matchDao.observe(key).map { it?.toDomain() }

    suspend fun refreshMatch(matchKey: String) {
        try {
            val dto = api.getMatch(matchKey)
            matchDao.insertAll(listOf(dto.toEntity()))
        } catch (_: Exception) { }
    }

    suspend fun refreshEventMatches(eventKey: String) {
        try {
            val dtos = api.getEventMatches(eventKey)
            db.withTransaction {
                matchDao.deleteByEvent(eventKey)
                matchDao.insertAll(dtos.map { it.toEntity() })
            }
        } catch (_: Exception) { }
    }
}
