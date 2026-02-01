package com.thebluealliance.android.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.thebluealliance.android.data.local.entity.MatchEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MatchDao {
    @Query("SELECT * FROM matches WHERE eventKey = :eventKey ORDER BY compLevel, setNumber, matchNumber ASC")
    fun observeByEvent(eventKey: String): Flow<List<MatchEntity>>

    @Query("SELECT * FROM matches WHERE key = :key")
    fun observe(key: String): Flow<MatchEntity?>

    @Query("DELETE FROM matches WHERE eventKey = :eventKey")
    suspend fun deleteByEvent(eventKey: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(matches: List<MatchEntity>)
}
