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

    @Query("DELETE FROM event_teams WHERE eventKey = :eventKey")
    suspend fun deleteByEvent(eventKey: String)
}
