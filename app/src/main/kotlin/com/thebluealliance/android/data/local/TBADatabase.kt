package com.thebluealliance.android.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.thebluealliance.android.data.local.dao.AllianceDao
import com.thebluealliance.android.data.local.dao.AwardDao
import com.thebluealliance.android.data.local.dao.DistrictDao
import com.thebluealliance.android.data.local.dao.DistrictRankingDao
import com.thebluealliance.android.data.local.dao.EventCOPRsDao
import com.thebluealliance.android.data.local.dao.EventDao
import com.thebluealliance.android.data.local.dao.EventDistrictPointsDao
import com.thebluealliance.android.data.local.dao.EventInsightsDao
import com.thebluealliance.android.data.local.dao.EventOPRsDao
import com.thebluealliance.android.data.local.dao.EventRankingSortOrderDao
import com.thebluealliance.android.data.local.dao.EventTeamDao
import com.thebluealliance.android.data.local.dao.FavoriteDao
import com.thebluealliance.android.data.local.dao.MatchDao
import com.thebluealliance.android.data.local.dao.MediaDao
import com.thebluealliance.android.data.local.dao.RankingDao
import com.thebluealliance.android.data.local.dao.SubscriptionDao
import com.thebluealliance.android.data.local.dao.TeamDao
import com.thebluealliance.android.data.local.dao.TeamEventStatusDao
import com.thebluealliance.android.data.local.entity.AllianceEntity
import com.thebluealliance.android.data.local.entity.AwardEntity
import com.thebluealliance.android.data.local.entity.DistrictEntity
import com.thebluealliance.android.data.local.entity.DistrictRankingEntity
import com.thebluealliance.android.data.local.entity.EventCOPRsEntity
import com.thebluealliance.android.data.local.entity.EventDistrictPointsEntity
import com.thebluealliance.android.data.local.entity.EventEntity
import com.thebluealliance.android.data.local.entity.EventInsightsEntity
import com.thebluealliance.android.data.local.entity.EventOPRsEntity
import com.thebluealliance.android.data.local.entity.EventRankingSortOrderEntity
import com.thebluealliance.android.data.local.entity.EventTeamEntity
import com.thebluealliance.android.data.local.entity.FavoriteEntity
import com.thebluealliance.android.data.local.entity.MatchEntity
import com.thebluealliance.android.data.local.entity.MediaEntity
import com.thebluealliance.android.data.local.entity.RankingEntity
import com.thebluealliance.android.data.local.entity.SubscriptionEntity
import com.thebluealliance.android.data.local.entity.TeamEntity
import com.thebluealliance.android.data.local.entity.TeamEventStatusEntity

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
        EventOPRsEntity::class,
        EventCOPRsEntity::class,
        EventInsightsEntity::class,
        EventRankingSortOrderEntity::class,
        TeamEventStatusEntity::class,
    ],
    version = 16,
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

    abstract fun eventOPRsDao(): EventOPRsDao

    abstract fun eventCOPRsDao(): EventCOPRsDao

    abstract fun eventInsightsDao(): EventInsightsDao

    abstract fun eventRankingSortOrderDao(): EventRankingSortOrderDao

    abstract fun teamEventStatusDao(): TeamEventStatusDao
}
