package com.thebluealliance.android.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.thebluealliance.android.data.local.entity.MatchEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MatchDao {
    @Query(
        // CASE mirrors CompLevel.order; sorting the TEXT codes alphabetically puts finals first
        "SELECT * FROM matches WHERE eventKey = :eventKey ORDER BY CASE compLevel WHEN 'qm' THEN 0 WHEN 'ef' THEN 1 WHEN 'qf' THEN 2 WHEN 'sf' THEN 3 WHEN 'f' THEN 4 ELSE 5 END, setNumber, matchNumber ASC",
    )
    fun observeByEvent(eventKey: String): Flow<List<MatchEntity>>

    @Query("SELECT * FROM matches WHERE key = :key")
    fun observe(key: String): Flow<MatchEntity?>

    @Query("DELETE FROM matches WHERE eventKey = :eventKey")
    suspend fun deleteByEvent(eventKey: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(matches: List<MatchEntity>)
}
