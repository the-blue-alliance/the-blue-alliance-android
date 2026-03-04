package com.thebluealliance.android.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.thebluealliance.android.data.local.entity.EventOPRsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EventOPRsDao {
    @Query("SELECT * FROM event_oprs WHERE eventKey = :eventKey")
    fun observe(eventKey: String): Flow<EventOPRsEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(oprs: EventOPRsEntity)

    @Query("DELETE FROM event_oprs WHERE eventKey = :eventKey")
    suspend fun delete(eventKey: String)
}
