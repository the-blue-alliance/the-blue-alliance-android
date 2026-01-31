package com.thebluealliance.android.data.repository

import com.thebluealliance.android.data.local.dao.AllianceDao
import com.thebluealliance.android.data.local.dao.AwardDao
import com.thebluealliance.android.data.local.dao.EventDao
import com.thebluealliance.android.data.local.dao.EventTeamDao
import com.thebluealliance.android.data.local.dao.RankingDao
import com.thebluealliance.android.data.local.entity.EventTeamEntity
import com.thebluealliance.android.data.mappers.*
import com.thebluealliance.android.data.remote.TbaApi
import com.thebluealliance.android.domain.model.Alliance
import com.thebluealliance.android.domain.model.Award
import com.thebluealliance.android.domain.model.Event
import com.thebluealliance.android.domain.model.Ranking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
@Singleton
class EventRepository @Inject constructor(
    private val api: TbaApi,
    private val eventDao: EventDao,
    private val awardDao: AwardDao,
    private val rankingDao: RankingDao,
    private val allianceDao: AllianceDao,
    private val eventTeamDao: EventTeamDao,
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

    fun observeTeamEventKeys(teamKey: String): Flow<List<String>> =
        eventTeamDao.observeByTeam(teamKey).map { list -> list.map { it.eventKey } }

    fun observeTeamEvents(teamKey: String, year: Int): Flow<List<Event>> =
        eventTeamDao.observeByTeam(teamKey).map { list ->
            list.map { it.eventKey }.filter { it.startsWith(year.toString()) }
        }.flatMapLatest { keys ->
            if (keys.isEmpty()) flowOf(emptyList())
            else eventDao.observeByKeys(keys).map { entities -> entities.map { it.toDomain() } }
        }

    suspend fun refreshTeamEvents(teamKey: String, year: Int) {
        try {
            val dtos = api.getTeamEvents(teamKey, year)
            eventDao.insertAll(dtos.map { it.toEntity() })
            val eventTeams = dtos.map { EventTeamEntity(eventKey = it.key, teamKey = teamKey) }
            eventTeamDao.deleteByTeam(teamKey)
            eventTeamDao.insertAll(eventTeams)
        } catch (_: Exception) { }
    }

    suspend fun refreshEventsForYear(year: Int) {
        val dtos = api.getEventsForYear(year)
        eventDao.insertAll(dtos.map { it.toEntity() })
    }

    suspend fun refreshEventAwards(eventKey: String) {
        try {
            val dtos = api.getEventAwards(eventKey)
            awardDao.insertAll(dtos.flatMap { it.toEntities() })
        } catch (_: Exception) { }
    }

    suspend fun refreshEventRankings(eventKey: String) {
        try {
            val response = api.getEventRankings(eventKey)
            rankingDao.deleteByEvent(eventKey)
            rankingDao.insertAll(response.rankings.map { it.toEntity(eventKey) })
        } catch (_: Exception) { }
    }

    suspend fun refreshEventAlliances(eventKey: String) {
        try {
            val dtos = api.getEventAlliances(eventKey)
            allianceDao.deleteByEvent(eventKey)
            allianceDao.insertAll(dtos.mapIndexed { index, dto -> dto.toEntity(eventKey, index + 1) })
        } catch (_: Exception) { }
    }
}
