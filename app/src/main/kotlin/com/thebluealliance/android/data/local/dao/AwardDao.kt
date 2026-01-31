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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(awards: List<AwardEntity>)
}
