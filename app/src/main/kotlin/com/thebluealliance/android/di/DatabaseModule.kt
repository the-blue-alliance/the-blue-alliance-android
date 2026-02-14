package com.thebluealliance.android.di

import android.content.Context
import androidx.room.Room
import com.thebluealliance.android.data.local.TBADatabase
import com.thebluealliance.android.data.local.dao.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): TBADatabase =
        Room.databaseBuilder(context, TBADatabase::class.java, "tba.db")
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()

    @Provides fun provideTeamDao(db: TBADatabase): TeamDao = db.teamDao()
    @Provides fun provideEventDao(db: TBADatabase): EventDao = db.eventDao()
    @Provides fun provideMatchDao(db: TBADatabase): MatchDao = db.matchDao()
    @Provides fun provideAwardDao(db: TBADatabase): AwardDao = db.awardDao()
    @Provides fun provideRankingDao(db: TBADatabase): RankingDao = db.rankingDao()
    @Provides fun provideAllianceDao(db: TBADatabase): AllianceDao = db.allianceDao()
    @Provides fun provideDistrictDao(db: TBADatabase): DistrictDao = db.districtDao()
    @Provides fun provideDistrictRankingDao(db: TBADatabase): DistrictRankingDao = db.districtRankingDao()
    @Provides fun provideRegionalRankingDao(db: TBADatabase): RegionalRankingDao = db.regionalRankingDao()
    @Provides fun provideMediaDao(db: TBADatabase): MediaDao = db.mediaDao()
    @Provides fun provideEventTeamDao(db: TBADatabase): EventTeamDao = db.eventTeamDao()
    @Provides fun provideFavoriteDao(db: TBADatabase): FavoriteDao = db.favoriteDao()
    @Provides fun provideSubscriptionDao(db: TBADatabase): SubscriptionDao = db.subscriptionDao()
    @Provides fun provideEventDistrictPointsDao(db: TBADatabase): EventDistrictPointsDao = db.eventDistrictPointsDao()
}
