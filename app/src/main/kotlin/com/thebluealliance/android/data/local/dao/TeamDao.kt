package com.thebluealliance.android.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.thebluealliance.android.data.local.entity.TeamEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TeamDao {
    @Query("SELECT * FROM teams ORDER BY number ASC")
    fun observeAll(): Flow<List<TeamEntity>>

    @Query("SELECT * FROM teams WHERE key = :key")
    fun observe(key: String): Flow<TeamEntity?>

    @Query("SELECT * FROM teams WHERE key IN (:keys) ORDER BY number ASC")
    fun observeByKeys(keys: List<String>): Flow<List<TeamEntity>>

    @Query("SELECT * FROM teams WHERE nickname LIKE '%' || :query || '%' OR name LIKE '%' || :query || '%' OR key LIKE '%' || :query || '%' OR CAST(number AS TEXT) = :query ORDER BY number ASC LIMIT 50")
    fun search(query: String): Flow<List<TeamEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(teams: List<TeamEntity>)
}
