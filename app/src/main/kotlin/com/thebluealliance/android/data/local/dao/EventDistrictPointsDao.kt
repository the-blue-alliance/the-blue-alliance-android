package com.thebluealliance.android.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.thebluealliance.android.data.local.entity.EventDistrictPointsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDistrictPointsDao {
    @Query("SELECT * FROM event_district_points WHERE eventKey = :eventKey ORDER BY total DESC")
    fun observeByEvent(eventKey: String): Flow<List<EventDistrictPointsEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(points: List<EventDistrictPointsEntity>)

    @Query("DELETE FROM event_district_points WHERE eventKey = :eventKey")
    suspend fun deleteByEvent(eventKey: String)
}
