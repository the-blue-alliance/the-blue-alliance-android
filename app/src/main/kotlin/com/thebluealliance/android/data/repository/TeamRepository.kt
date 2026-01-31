package com.thebluealliance.android.data.repository

import com.thebluealliance.android.data.local.dao.MediaDao
import com.thebluealliance.android.data.local.dao.TeamDao
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
class TeamRepository @Inject constructor(
    private val api: TbaApi,
    private val teamDao: TeamDao,
    private val mediaDao: MediaDao,
) {
    fun observeTeam(key: String): Flow<Team?> =
        teamDao.observe(key).map { it?.toDomain() }

    fun observeTeams(keys: List<String>): Flow<List<Team>> =
        teamDao.observeByKeys(keys).map { list -> list.map { it.toDomain() } }

    fun observeTeamMedia(teamKey: String, year: Int): Flow<List<Media>> =
        mediaDao.observeByTeamYear(teamKey, year).map { list -> list.map { it.toDomain() } }

    suspend fun refreshTeamsPage(page: Int) {
        val dtos = api.getTeams(page)
        teamDao.insertAll(dtos.map { it.toEntity() })
    }

    suspend fun refreshEventTeams(eventKey: String) {
        val dtos = api.getEventTeams(eventKey)
        teamDao.insertAll(dtos.map { it.toEntity() })
    }

    suspend fun refreshTeamMedia(teamKey: String, year: Int) {
        val dtos = api.getTeamMedia(teamKey, year)
        mediaDao.insertAll(dtos.map { it.toEntity(teamKey, year) })
    }
}
