package com.thebluealliance.android.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.thebluealliance.android.data.local.entity.TeamEventStatusEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TeamEventStatusDao {
    @Query("SELECT * FROM team_event_status WHERE teamKey = :teamKey AND eventKey = :eventKey")
    fun observe(
        teamKey: String,
        eventKey: String,
    ): Flow<TeamEventStatusEntity?>

    @Query("SELECT * FROM team_event_status WHERE eventKey = :eventKey")
    fun observeByEvent(eventKey: String): Flow<List<TeamEventStatusEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(status: TeamEventStatusEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(statuses: List<TeamEventStatusEntity>)

    @Query("DELETE FROM team_event_status WHERE eventKey = :eventKey")
    suspend fun deleteByEvent(eventKey: String)
}
