package com.thebluealliance.android.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.thebluealliance.android.data.local.entity.DistrictRankingEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DistrictRankingDao {
    @Query("SELECT * FROM district_rankings WHERE districtKey = :districtKey ORDER BY rank ASC")
    fun observeByDistrict(districtKey: String): Flow<List<DistrictRankingEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(rankings: List<DistrictRankingEntity>)

    @Query("DELETE FROM district_rankings WHERE districtKey = :districtKey")
    suspend fun deleteByDistrict(districtKey: String)
}
