package com.thebluealliance.android.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.thebluealliance.android.data.local.entity.EventAdvancementPointsEntity
import com.thebluealliance.android.data.local.entity.PointsSource
import kotlinx.coroutines.flow.Flow

@Dao
interface EventAdvancementPointsDao {
    @Query(
        "SELECT * FROM event_advancement_points WHERE eventKey = :eventKey AND source = :source ORDER BY total DESC",
    )
    fun observeByEvent(
        eventKey: String,
        source: PointsSource,
    ): Flow<List<EventAdvancementPointsEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(points: List<EventAdvancementPointsEntity>)

    @Query("DELETE FROM event_advancement_points WHERE eventKey = :eventKey AND source = :source")
    suspend fun deleteByEvent(
        eventKey: String,
        source: PointsSource,
    )
}
