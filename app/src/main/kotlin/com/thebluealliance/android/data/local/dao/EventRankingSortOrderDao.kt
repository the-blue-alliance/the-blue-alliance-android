package com.thebluealliance.android.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.thebluealliance.android.data.local.entity.EventRankingSortOrderEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EventRankingSortOrderDao {
    @Query("SELECT * FROM event_ranking_sort_orders WHERE eventKey = :eventKey")
    fun observe(eventKey: String): Flow<EventRankingSortOrderEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: EventRankingSortOrderEntity)

    @Query("DELETE FROM event_ranking_sort_orders WHERE eventKey = :eventKey")
    suspend fun delete(eventKey: String)
}

