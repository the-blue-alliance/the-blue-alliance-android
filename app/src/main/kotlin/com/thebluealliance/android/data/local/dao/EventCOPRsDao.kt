package com.thebluealliance.android.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.thebluealliance.android.data.local.entity.EventCOPRsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EventCOPRsDao {
    @Query("SELECT * FROM event_coprs WHERE eventKey = :eventKey")
    fun observe(eventKey: String): Flow<EventCOPRsEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(coprs: EventCOPRsEntity)

    @Query("DELETE FROM event_coprs WHERE eventKey = :eventKey")
    suspend fun delete(eventKey: String)
}

