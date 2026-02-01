package com.thebluealliance.android.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.thebluealliance.android.data.local.entity.AwardEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AwardDao {
    @Query("SELECT * FROM awards WHERE eventKey = :eventKey")
    fun observeByEvent(eventKey: String): Flow<List<AwardEntity>>

    @Query("DELETE FROM awards WHERE eventKey = :eventKey")
    suspend fun deleteByEvent(eventKey: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(awards: List<AwardEntity>)
}
