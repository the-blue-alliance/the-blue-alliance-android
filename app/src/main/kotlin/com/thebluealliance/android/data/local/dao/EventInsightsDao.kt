package com.thebluealliance.android.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.thebluealliance.android.data.local.entity.EventInsightsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EventInsightsDao {
    @Query("SELECT * FROM event_insights WHERE eventKey = :eventKey")
    fun observe(eventKey: String): Flow<EventInsightsEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(insights: EventInsightsEntity)

    @Query("DELETE FROM event_insights WHERE eventKey = :eventKey")
    suspend fun delete(eventKey: String)
}

