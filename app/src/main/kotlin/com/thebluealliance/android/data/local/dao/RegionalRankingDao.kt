package com.thebluealliance.android.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.thebluealliance.android.data.local.entity.RegionalRankingEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RegionalRankingDao {
    @Query("SELECT * FROM regional_rankings WHERE year = :year ORDER BY rank ASC")
    fun observeByYear(year: Int): Flow<List<RegionalRankingEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(rankings: List<RegionalRankingEntity>)

    @Query("DELETE FROM regional_rankings WHERE year = :year")
    suspend fun deleteByYear(year: Int)
}
