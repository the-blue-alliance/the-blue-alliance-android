package com.thebluealliance.android.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.thebluealliance.android.data.local.entity.EventEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {
    @Query("SELECT * FROM events WHERE year = :year ORDER BY startDate ASC")
    fun observeByYear(year: Int): Flow<List<EventEntity>>

    @Query("SELECT * FROM events WHERE key = :key")
    fun observe(key: String): Flow<EventEntity?>

    @Query("SELECT * FROM events WHERE key IN (:keys) ORDER BY startDate ASC")
    fun observeByKeys(keys: List<String>): Flow<List<EventEntity>>

    @Query("SELECT * FROM events WHERE district = :districtKey ORDER BY startDate ASC")
    fun observeByDistrict(districtKey: String): Flow<List<EventEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(events: List<EventEntity>)
}
