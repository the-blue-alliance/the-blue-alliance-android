package com.thebluealliance.android.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.thebluealliance.android.data.local.entity.AllianceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AllianceDao {
    @Query("SELECT * FROM alliances WHERE eventKey = :eventKey ORDER BY number ASC")
    fun observeByEvent(eventKey: String): Flow<List<AllianceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(alliances: List<AllianceEntity>)

    @Query("DELETE FROM alliances WHERE eventKey = :eventKey")
    suspend fun deleteByEvent(eventKey: String)
}
