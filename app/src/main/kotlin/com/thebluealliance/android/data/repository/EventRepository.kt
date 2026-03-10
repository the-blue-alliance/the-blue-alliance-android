package com.thebluealliance.android.data.repository

import androidx.room.withTransaction
import com.thebluealliance.android.data.local.TBADatabase
import com.thebluealliance.android.data.local.dao.AllianceDao
import com.thebluealliance.android.data.local.dao.AwardDao
import com.thebluealliance.android.data.local.dao.EventCOPRsDao
import com.thebluealliance.android.data.local.dao.EventDao
import com.thebluealliance.android.data.local.dao.EventDistrictPointsDao
import com.thebluealliance.android.data.local.dao.EventInsightsDao
import com.thebluealliance.android.data.local.dao.EventOPRsDao
import com.thebluealliance.android.data.local.dao.EventRankingSortOrderDao
import com.thebluealliance.android.data.local.dao.EventTeamDao
import com.thebluealliance.android.data.local.dao.RankingDao
import com.thebluealliance.android.data.local.entity.EventTeamEntity
import com.thebluealliance.android.data.mappers.*
import com.thebluealliance.android.data.remote.TbaApi
import com.thebluealliance.android.data.remote.dto.EventInsightsDto
import com.thebluealliance.android.domain.model.Alliance
import com.thebluealliance.android.domain.model.Award
import com.thebluealliance.android.domain.model.Event
import com.thebluealliance.android.domain.model.EventCOPRs
import com.thebluealliance.android.domain.model.EventDistrictPoints
import com.thebluealliance.android.domain.model.EventInsights
import com.thebluealliance.android.domain.model.EventOPRs
import com.thebluealliance.android.domain.model.EventRankings
import com.thebluealliance.android.domain.model.Ranking
import com.thebluealliance.android.domain.model.RankingSortOrder
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
    private val db: TBADatabase,
    private val eventDao: EventDao,
    private val awardDao: AwardDao,
    private val rankingDao: RankingDao,
    private val allianceDao: AllianceDao,
    private val eventTeamDao: EventTeamDao,
    private val eventDistrictPointsDao: EventDistrictPointsDao,
    private val eventOPRsDao: EventOPRsDao,
    private val eventCOPRsDao: EventCOPRsDao,
    private val eventInsightsDao: EventInsightsDao,
    private val eventRankingSortOrderDao: EventRankingSortOrderDao,
) {
    fun searchEvents(query: String): Flow<List<Event>> =
        eventDao.search(query).map { list -> list.map { it.toDomain() } }

    fun observeEventsForYear(year: Int): Flow<List<Event>> =
        eventDao.observeByYear(year).map { list -> list.map { it.toDomain() } }

    fun observeEvent(key: String): Flow<Event?> =
        eventDao.observe(key).map { it?.toDomain() }

    fun observeEventAwards(eventKey: String): Flow<List<Award>> =
        awardDao.observeByEvent(eventKey).map { list -> list.map { it.toDomain() } }

    fun observeEventRankings(eventKey: String): Flow<List<Ranking>> =
        rankingDao.observeByEvent(eventKey).map { list -> list.map { it.toDomain() } }

    fun observeEventRankingSortOrders(eventKey: String): Flow<List<RankingSortOrder>> =
        eventRankingSortOrderDao.observe(eventKey).map { entity ->
            entity?.getSortOrderInfo() ?: emptyList()
        }

    fun observeEventRankingExtraStatsInfo(eventKey: String): Flow<List<RankingSortOrder>> =
        eventRankingSortOrderDao.observe(eventKey).map { entity ->
            entity?.getExtraStatsInfo() ?: emptyList()
        }

    fun observeEventRankingsWithSortOrders(eventKey: String): Flow<EventRankings?> =
        rankingDao.observeByEvent(eventKey).flatMapLatest { rankingEntities ->
            eventRankingSortOrderDao.observe(eventKey).map { sortOrderEntity ->
                if (rankingEntities.isEmpty() || sortOrderEntity == null) {
                    null
                } else {
                    EventRankings(
                        rankings = rankingEntities.map { it.toDomain() },
                        sortOrderInfo = sortOrderEntity.getSortOrderInfo(),
                        extraStatsInfo = sortOrderEntity.getExtraStatsInfo(),
                    )
                }
            }
        }

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
            db.withTransaction {
                eventDao.insertAll(dtos.map { it.toEntity() })
                eventTeamDao.deleteByTeamAndYear(teamKey, year.toString())
                eventTeamDao.insertAll(dtos.map { EventTeamEntity(eventKey = it.key, teamKey = teamKey) })
            }
        } catch (_: Exception) { }
    }

    fun observeDistrictEvents(districtKey: String): Flow<List<Event>> =
        eventDao.observeByDistrict(districtKey).map { list -> list.map { it.toDomain() } }

    suspend fun refreshDistrictEvents(districtKey: String) {
        try {
            val dtos = api.getDistrictEvents(districtKey)
            db.withTransaction {
                eventDao.deleteByDistrict(districtKey)
                eventDao.insertAll(dtos.map { it.toEntity() })
            }
        } catch (_: Exception) { }
    }

    suspend fun refreshEvent(eventKey: String) {
        val dto = api.getEvent(eventKey)
        eventDao.insertAll(listOf(dto.toEntity()))
    }

    suspend fun refreshEventsForYear(year: Int) {
        val dtos = api.getEventsForYear(year)
        db.withTransaction {
            eventDao.deleteByYear(year)
            eventDao.insertAll(dtos.map { it.toEntity() })
        }
    }

    suspend fun refreshEventAwards(eventKey: String) {
        try {
            val dtos = api.getEventAwards(eventKey)
            db.withTransaction {
                awardDao.deleteByEvent(eventKey)
                awardDao.insertAll(dtos.flatMap { it.toEntities() })
            }
        } catch (_: Exception) { }
    }

    suspend fun refreshEventRankings(eventKey: String) {
        try {
            val response = api.getEventRankings(eventKey)
            db.withTransaction {
                rankingDao.deleteByEvent(eventKey)
                rankingDao.insertAll(response.rankings.map { it.toEntity(eventKey) })
                eventRankingSortOrderDao.delete(eventKey)
                eventRankingSortOrderDao.insert(response.toSortOrderEntity(eventKey))
            }
        } catch (_: Exception) { }
    }

    fun observeEventDistrictPoints(eventKey: String): Flow<List<EventDistrictPoints>> =
        eventDistrictPointsDao.observeByEvent(eventKey).map { list -> list.map { it.toDomain() } }

    suspend fun refreshEventDistrictPoints(eventKey: String) {
        try {
            val response = api.getEventDistrictPoints(eventKey)
            val entities = response.points.map { (teamKey, entry) ->
                entry.toEntity(eventKey, teamKey)
            }
            db.withTransaction {
                eventDistrictPointsDao.deleteByEvent(eventKey)
                eventDistrictPointsDao.insertAll(entities)
            }
        } catch (_: Exception) { }
    }

    suspend fun refreshEventAlliances(eventKey: String) {
        try {
            val dtos = api.getEventAlliances(eventKey)
            db.withTransaction {
                allianceDao.deleteByEvent(eventKey)
                allianceDao.insertAll(dtos.mapIndexed { index, dto -> dto.toEntity(eventKey, index + 1) })
            }
        } catch (_: Exception) { }
    }

    fun observeEventOPRs(eventKey: String): Flow<EventOPRs?> =
        eventOPRsDao.observe(eventKey).map { it?.toDomain() }

    suspend fun refreshEventOPRs(eventKey: String) {
        try {
            val dto = api.getEventOPRs(eventKey)
            db.withTransaction {
                eventOPRsDao.delete(eventKey)
                eventOPRsDao.insert(dto.toEntity(eventKey))
            }
        } catch (_: Exception) { }
    }

    fun observeEventCOPRs(eventKey: String): Flow<EventCOPRs?> =
        eventCOPRsDao.observe(eventKey).map { it?.toDomain() }

    suspend fun refreshEventCOPRs(eventKey: String) {
        try {
            val dto = api.getEventCOPRs(eventKey)
            db.withTransaction {
                eventCOPRsDao.delete(eventKey)
                eventCOPRsDao.insert(dto.toEntity(eventKey))
            }
        } catch (_: Exception) { }
    }

    fun observeEventInsights(eventKey: String): Flow<EventInsights?> =
        eventInsightsDao.observe(eventKey).map { it?.toDomain() }

    suspend fun refreshEventInsights(eventKey: String) {
        try {
            val insights = api.getEventInsights(eventKey)
            val dto = EventInsightsDto(
                qual = insights["qual"]?.toString(),
                playoff = insights["playoff"]?.toString(),
            )
            db.withTransaction {
                eventInsightsDao.delete(eventKey)
                eventInsightsDao.insert(dto.toEntity(eventKey))
            }
        } catch (_: Exception) { }
    }

    suspend fun fetchEventPitLocations(eventKey: String): Map<String, String> {
        return try {
            val statuses = api.getEventTeamsStatuses(eventKey)
            statuses.mapNotNull { (teamKey, status) ->
                status?.pitLocation?.let { teamKey to it }
            }.toMap()
        } catch (_: Exception) {
            emptyMap()
        }
    }
}
