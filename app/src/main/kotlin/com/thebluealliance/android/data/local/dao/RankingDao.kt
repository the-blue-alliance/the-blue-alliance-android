package com.thebluealliance.android.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.thebluealliance.android.data.local.entity.RankingEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RankingDao {
    @Query("SELECT * FROM rankings WHERE eventKey = :eventKey ORDER BY rank ASC")
    fun observeByEvent(eventKey: String): Flow<List<RankingEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(rankings: List<RankingEntity>)

    @Query("DELETE FROM rankings WHERE eventKey = :eventKey")
    suspend fun deleteByEvent(eventKey: String)
}
