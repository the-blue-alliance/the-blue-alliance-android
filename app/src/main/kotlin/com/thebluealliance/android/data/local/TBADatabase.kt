package com.thebluealliance.android.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.thebluealliance.android.data.local.dao.*
import com.thebluealliance.android.data.local.entity.*

@Database(
    entities = [
        TeamEntity::class,
        EventEntity::class,
        MatchEntity::class,
        AwardEntity::class,
        RankingEntity::class,
        AllianceEntity::class,
        DistrictEntity::class,
        DistrictRankingEntity::class,
        MediaEntity::class,
        EventTeamEntity::class,
        FavoriteEntity::class,
        SubscriptionEntity::class,
        EventDistrictPointsEntity::class,
    ],
    version = 5,
    exportSchema = false,
)
abstract class TBADatabase : RoomDatabase() {
    abstract fun teamDao(): TeamDao
    abstract fun eventDao(): EventDao
    abstract fun matchDao(): MatchDao
    abstract fun awardDao(): AwardDao
    abstract fun rankingDao(): RankingDao
    abstract fun allianceDao(): AllianceDao
    abstract fun districtDao(): DistrictDao
    abstract fun districtRankingDao(): DistrictRankingDao
    abstract fun mediaDao(): MediaDao
    abstract fun eventTeamDao(): EventTeamDao
    abstract fun favoriteDao(): FavoriteDao
    abstract fun subscriptionDao(): SubscriptionDao
    abstract fun eventDistrictPointsDao(): EventDistrictPointsDao
}
