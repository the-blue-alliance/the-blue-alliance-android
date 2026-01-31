package com.thebluealliance.android.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.thebluealliance.android.data.local.entity.EventTeamEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EventTeamDao {
    @Query("SELECT * FROM event_teams WHERE eventKey = :eventKey ORDER BY teamKey ASC")
    fun observeByEvent(eventKey: String): Flow<List<EventTeamEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(eventTeams: List<EventTeamEntity>)

    @Query("SELECT * FROM event_teams WHERE teamKey = :teamKey ORDER BY eventKey ASC")
    fun observeByTeam(teamKey: String): Flow<List<EventTeamEntity>>

    @Query("DELETE FROM event_teams WHERE eventKey = :eventKey")
    suspend fun deleteByEvent(eventKey: String)

    @Query("DELETE FROM event_teams WHERE teamKey = :teamKey")
    suspend fun deleteByTeam(teamKey: String)
}
