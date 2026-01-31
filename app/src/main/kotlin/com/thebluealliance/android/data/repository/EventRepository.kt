package com.thebluealliance.android.data.repository

import com.thebluealliance.android.data.local.dao.AllianceDao
import com.thebluealliance.android.data.local.dao.AwardDao
import com.thebluealliance.android.data.local.dao.EventDao
import com.thebluealliance.android.data.local.dao.RankingDao
import com.thebluealliance.android.data.mappers.*
import com.thebluealliance.android.data.remote.TbaApi
import com.thebluealliance.android.domain.model.Alliance
import com.thebluealliance.android.domain.model.Award
import com.thebluealliance.android.domain.model.Event
import com.thebluealliance.android.domain.model.Ranking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EventRepository @Inject constructor(
    private val api: TbaApi,
    private val eventDao: EventDao,
    private val awardDao: AwardDao,
    private val rankingDao: RankingDao,
    private val allianceDao: AllianceDao,
) {
    fun observeEventsForYear(year: Int): Flow<List<Event>> =
        eventDao.observeByYear(year).map { list -> list.map { it.toDomain() } }

    fun observeEvent(key: String): Flow<Event?> =
        eventDao.observe(key).map { it?.toDomain() }

    fun observeEventAwards(eventKey: String): Flow<List<Award>> =
        awardDao.observeByEvent(eventKey).map { list -> list.map { it.toDomain() } }

    fun observeEventRankings(eventKey: String): Flow<List<Ranking>> =
        rankingDao.observeByEvent(eventKey).map { list -> list.map { it.toDomain() } }

    fun observeEventAlliances(eventKey: String): Flow<List<Alliance>> =
        allianceDao.observeByEvent(eventKey).map { list -> list.map { it.toDomain() } }

    suspend fun refreshEventsForYear(year: Int) {
        val dtos = api.getEventsForYear(year)
        eventDao.insertAll(dtos.map { it.toEntity() })
    }

    suspend fun refreshEventAwards(eventKey: String) {
        val dtos = api.getEventAwards(eventKey)
        awardDao.insertAll(dtos.flatMap { it.toEntities() })
    }

    suspend fun refreshEventRankings(eventKey: String) {
        val response = api.getEventRankings(eventKey)
        rankingDao.deleteByEvent(eventKey)
        rankingDao.insertAll(response.rankings.map { it.toEntity(eventKey) })
    }

    suspend fun refreshEventAlliances(eventKey: String) {
        val dtos = api.getEventAlliances(eventKey)
        allianceDao.deleteByEvent(eventKey)
        allianceDao.insertAll(dtos.mapIndexed { index, dto -> dto.toEntity(eventKey, index + 1) })
    }
}
