package com.thebluealliance.androidclient.database.writers;

import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.database.DatabaseWriter;

import javax.inject.Singleton;

import dagger.Lazy;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@InstallIn(SingletonComponent.class)
@Module
public class DatabaseWriterModule {

    @Provides @Singleton
    public AwardListWriter awardListWriter(Database db) {
        return new AwardListWriter(db);
    }

    @Provides @Singleton
    public AwardWriter awardWriter(Database db) {
        return new AwardWriter(db);
    }

    @Provides @Singleton
    public DistrictListWriter districtListWriter(Database db) {
        return new DistrictListWriter(db);
    }

    @Provides @Singleton
    public DistrictTeamListWriter districtTeamListWriter(Database db) {
        return new DistrictTeamListWriter(db);
    }

    @Provides @Singleton
    public DistrictTeamWriter districtTeamWriter(Database db) {
        return new DistrictTeamWriter(db);
    }

    @Provides @Singleton
    public DistrictWriter districtWriter(Database db) {
        return new DistrictWriter(db);
    }

    @Provides @Singleton
    public EventListWriter eventListWriter(Database db) {
        return new EventListWriter(db);
    }

    @Provides @Singleton
    public EventTeamListWriter eventTeamListWriter(Database db) {
        return new EventTeamListWriter(db);
    }

    @Provides @Singleton
    public EventTeamWriter eventTeamWriter(Database db) {
        return new EventTeamWriter(db);
    }

    @Provides @Singleton
    public EventWriter eventWriter(Database db) {
        return new EventWriter(db);
    }

    @Provides @Singleton
    public MatchListWriter matchListWriter(Database db) {
        return new MatchListWriter(db);
    }

    @Provides @Singleton
    public MatchWriter matchWriter(Database db) {
        return new MatchWriter(db);
    }

    @Provides @Singleton
    public MediaListWriter mediaListWriter(Database db) {
        return new MediaListWriter(db);
    }

    @Provides @Singleton
    public MediaWriter mediaWriter(Database db) {
        return new MediaWriter(db);
    }

    @Provides @Singleton
    public TeamListWriter teamListWriter(Database db) {
        return new TeamListWriter(db);
    }

    @Provides @Singleton
    public TeamWriter teamWriter(Database db) {
        return new TeamWriter(db);
    }

    @Provides @Singleton
    public YearsParticipatedWriter yearsParticipatedWriter(Database db, TeamWriter teamWriter) {
        return new YearsParticipatedWriter(db, teamWriter);
    }

    @Provides @Singleton
    public EventTeamAndTeamListWriter provideEventTeamAndTeamListWriter(
      Database database,
      EventTeamListWriter eventTeamListWriter,
      TeamListWriter teamListWriter) {
        return new EventTeamAndTeamListWriter(database, eventTeamListWriter, teamListWriter);
    }

    @Provides @Singleton
    public EventDetailWriter provideEventDetailWriter(Database db) {
        return new EventDetailWriter(db);
    }

    @Provides @Singleton
    public FavoriteCollectionWriter provideFavoriteCollectionWriter(Database db) {
        return new FavoriteCollectionWriter(db);
    }


    @Provides
    @Singleton
    public DatabaseWriter provideDatabaseWriter(
            Lazy<AwardWriter> award,
            Lazy<AwardListWriter> awardList,
            Lazy<DistrictWriter> district,
            Lazy<DistrictListWriter> districtList,
            Lazy<DistrictTeamWriter> districtTeam,
            Lazy<DistrictTeamListWriter> districtTeamList,
            Lazy<EventWriter> event,
            Lazy<EventListWriter> eventList,
            Lazy<EventTeamWriter> eventTeam,
            Lazy<EventTeamListWriter> eventTeamList,
            Lazy<MatchWriter> match,
            Lazy<MatchListWriter> matchList,
            Lazy<MediaWriter> media,
            Lazy<MediaListWriter> mediaList,
            Lazy<TeamWriter> team,
            Lazy<TeamListWriter> teamList,
            Lazy<YearsParticipatedWriter> yearsParticipated,
            Lazy<EventTeamAndTeamListWriter> eventTeamAndTeamList,
            Lazy<EventDetailWriter> eventDetail) {
        return new DatabaseWriter(award, awardList, district, districtList, districtTeam,
                districtTeamList, event, eventList, eventTeam, eventTeamList, match, matchList, media,
                mediaList, team, teamList, yearsParticipated, eventTeamAndTeamList, eventDetail);
    }
}
