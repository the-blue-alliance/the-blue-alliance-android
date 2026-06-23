package com.thebluealliance.android.data.repository

import androidx.room.withTransaction
import com.thebluealliance.android.data.local.TBADatabase
import com.thebluealliance.android.data.local.dao.EventTeamDao
import com.thebluealliance.android.data.local.dao.MediaDao
import com.thebluealliance.android.data.local.dao.TeamDao
import com.thebluealliance.android.data.local.dao.TeamEventStatusDao
import com.thebluealliance.android.data.local.entity.EventTeamEntity
import com.thebluealliance.android.data.local.entity.TeamEventStatusEntity
import com.thebluealliance.android.data.mappers.toDomain
import com.thebluealliance.android.data.mappers.toEntity
import com.thebluealliance.android.data.remote.TbaApi
import com.thebluealliance.android.domain.model.Media
import com.thebluealliance.android.domain.model.Team
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TeamRepository
    @Inject
    constructor(
        private val api: TbaApi,
        private val db: TBADatabase,
        private val teamDao: TeamDao,
        private val mediaDao: MediaDao,
        private val eventTeamDao: EventTeamDao,
        private val teamEventStatusDao: TeamEventStatusDao,
    ) {
        fun searchTeams(query: String): Flow<List<Team>> =
            teamDao.search(query).map { list -> list.map { it.toDomain() } }

        fun observeAllTeams(): Flow<List<Team>> =
            teamDao.observeAll().map { list -> list.map { it.toDomain() } }

        fun observeTeam(key: String): Flow<Team?> = teamDao.observe(key).map { it?.toDomain() }

        fun observeTeams(keys: List<String>): Flow<List<Team>> =
            teamDao.observeByKeys(keys).map { list -> list.map { it.toDomain() } }

        fun observeTeamMedia(
            teamKey: String,
            year: Int,
        ): Flow<List<Media>> =
            mediaDao.observeByTeamYear(teamKey, year).map { list -> list.map { it.toDomain() } }

        /** @return number of teams fetched */
        suspend fun refreshTeamsPage(page: Int): Int {
            val dtos = api.getTeams(page)
            teamDao.insertAll(dtos.map { it.toEntity() })
            return dtos.size
        }

        /**
         * Pages through the full teams list until an empty page. The API pages by 500
         * team numbers, so a fixed page range goes stale as rookie numbers grow.
         */
        suspend fun refreshAllTeams() {
            var page = 0
            while (refreshTeamsPage(page) > 0) {
                page++
            }
        }

        fun observeEventTeamKeys(eventKey: String): Flow<List<String>> =
            eventTeamDao.observeByEvent(eventKey).map { list -> list.map { it.teamKey } }

        suspend fun refreshEventTeams(eventKey: String) {
            try {
                val dtos = api.getEventTeams(eventKey)
                db.withTransaction {
                    teamDao.insertAll(dtos.map { it.toEntity() })
                    eventTeamDao.deleteByEvent(eventKey)
                    eventTeamDao.insertAll(
                        dtos.map { EventTeamEntity(eventKey = eventKey, teamKey = it.key) },
                    )
                }
            } catch (_: Exception) {
            }
        }

        suspend fun refreshTeam(teamKey: String) {
            val dto = api.getTeam(teamKey)
            teamDao.insertAll(listOf(dto.toEntity()))
        }

        suspend fun refreshTeamMedia(
            teamKey: String,
            year: Int,
        ) {
            val dtos = api.getTeamMedia(teamKey, year)
            db.withTransaction {
                mediaDao.deleteByTeamYear(teamKey, year)
                mediaDao.insertAll(dtos.map { it.toEntity(teamKey, year) })
            }
        }

        fun observeTeamEventPitLocation(
            teamKey: String,
            eventKey: String,
        ): Flow<String?> = teamEventStatusDao.observe(teamKey, eventKey).map { it?.pitLocation }

        suspend fun refreshTeamEventPitLocation(
            teamKey: String,
            eventKey: String,
        ) {
            try {
                val pitLocation = api.getTeamEventStatus(teamKey, eventKey)?.pitLocation
                teamEventStatusDao.insert(TeamEventStatusEntity(teamKey, eventKey, pitLocation))
            } catch (_: Exception) {
            }
        }
    }
