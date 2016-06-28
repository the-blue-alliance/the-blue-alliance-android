package com.thebluealliance.androidclient.database.writers;

import com.squareup.sqlbrite.BriteDatabase;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.di.TBAAndroidModule;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(includes = TBAAndroidModule.class)
public class DatabaseWriterModule {

    @Provides @Singleton
    public AwardListWriter awardListWriter(Database db, BriteDatabase briteDb) {
        return new AwardListWriter(db, briteDb);
    }

    @Provides @Singleton
    public AwardWriter awardWriter(Database db, BriteDatabase briteDb) {
        return new AwardWriter(db, briteDb);
    }

    @Provides @Singleton
    public DistrictListWriter districtListWriter(Database db, BriteDatabase briteDb) {
        return new DistrictListWriter(db, briteDb);
    }

    @Provides @Singleton
    public DistrictTeamListWriter districtTeamListWriter(Database db, BriteDatabase briteDb) {
        return new DistrictTeamListWriter(db, briteDb);
    }

    @Provides @Singleton
    public DistrictTeamWriter districtTeamWriter(Database db, BriteDatabase briteDb) {
        return new DistrictTeamWriter(db, briteDb);
    }

    @Provides @Singleton
    public DistrictWriter districtWriter(Database db, BriteDatabase briteDb) {
        return new DistrictWriter(db, briteDb);
    }

    @Provides @Singleton
    public EventListWriter eventListWriter(Database db, BriteDatabase briteDb) {
        return new EventListWriter(db, briteDb);
    }

    @Provides @Singleton
    public EventTeamListWriter eventTeamListWriter(Database db, BriteDatabase briteDb) {
        return new EventTeamListWriter(db, briteDb);
    }

    @Provides @Singleton
    public EventTeamWriter eventTeamWriter(Database db, BriteDatabase briteDb) {
        return new EventTeamWriter(db, briteDb);
    }

    @Provides @Singleton
    public EventWriter eventWriter(Database db, BriteDatabase briteDb) {
        return new EventWriter(db, briteDb);
    }

    @Provides @Singleton
    public MatchListWriter matchListWriter(Database db, BriteDatabase briteDb) {
        return new MatchListWriter(db, briteDb);
    }

    @Provides @Singleton
    public MatchWriter matchWriter(Database db, BriteDatabase briteDb) {
        return new MatchWriter(db, briteDb);
    }

    @Provides @Singleton
    public MediaListWriter mediaListWriter(Database db, BriteDatabase briteDb) {
        return new MediaListWriter(db, briteDb);
    }

    @Provides @Singleton
    public MediaWriter mediaWriter(Database db, BriteDatabase briteDb) {
        return new MediaWriter(db, briteDb);
    }

    @Provides @Singleton
    public TeamListWriter teamListWriter(Database db, BriteDatabase briteDb) {
        return new TeamListWriter(db, briteDb);
    }

    @Provides @Singleton
    public TeamWriter teamWriter(Database db, BriteDatabase briteDb) {
        return new TeamWriter(db, briteDb);
    }

    @Provides @Singleton
    public YearsParticipatedWriter yearsParticipatedWriter(Database db, BriteDatabase briteDb, TeamWriter teamWriter) {
        return new YearsParticipatedWriter(db, briteDb, teamWriter);
    }

    @Provides @Singleton
    public EventTeamAndTeamListWriter provideEventTeamAndTeamListWriter(
            Database db,
            BriteDatabase briteDb,
            EventTeamListWriter eventTeamListWriter,
            TeamListWriter teamListWriter) {
        return new EventTeamAndTeamListWriter(db, briteDb, eventTeamListWriter, teamListWriter);
    }

    @Provides @Singleton
    public EventRankingsWriter provideEventRankingsWriter(Database db, BriteDatabase briteDb, EventWriter eventWriter) {
        return new EventRankingsWriter(db, briteDb, eventWriter);
    }

    @Provides @Singleton
    public EventStatsWriter provideEventStatsWriter(Database db, BriteDatabase briteDb, EventWriter eventWriter) {
        return new EventStatsWriter(db, briteDb, eventWriter);
    }

    @Provides @Singleton
    public EventDistrictPointsWriter provideEventDistrictPointsWriter(
            Database db,
            BriteDatabase briteDb,
            EventWriter eventWriter) {
        return new EventDistrictPointsWriter(db, briteDb, eventWriter);
    }
}
