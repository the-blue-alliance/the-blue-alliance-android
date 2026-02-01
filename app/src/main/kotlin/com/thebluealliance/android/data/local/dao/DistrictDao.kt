package com.thebluealliance.android.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.thebluealliance.android.data.local.entity.DistrictEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DistrictDao {
    @Query("SELECT * FROM districts WHERE year = :year ORDER BY displayName ASC")
    fun observeByYear(year: Int): Flow<List<DistrictEntity>>

    @Query("SELECT * FROM districts WHERE key = :key")
    fun observe(key: String): Flow<DistrictEntity?>

    @Query("DELETE FROM districts WHERE year = :year")
    suspend fun deleteByYear(year: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(districts: List<DistrictEntity>)
}
