package com.thebluealliance.androidclient.di;

import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.database.writers.AwardListWriter;
import com.thebluealliance.androidclient.database.writers.AwardWriter;
import com.thebluealliance.androidclient.database.writers.DistrictListWriter;
import com.thebluealliance.androidclient.database.writers.DistrictTeamListWriter;
import com.thebluealliance.androidclient.database.writers.DistrictTeamWriter;
import com.thebluealliance.androidclient.database.writers.DistrictWriter;
import com.thebluealliance.androidclient.database.writers.EventDistrictPointsWriter;
import com.thebluealliance.androidclient.database.writers.EventListWriter;
import com.thebluealliance.androidclient.database.writers.EventRankingsWriter;
import com.thebluealliance.androidclient.database.writers.EventStatsWriter;
import com.thebluealliance.androidclient.database.writers.EventTeamAndTeamListWriter;
import com.thebluealliance.androidclient.database.writers.EventTeamListWriter;
import com.thebluealliance.androidclient.database.writers.EventTeamWriter;
import com.thebluealliance.androidclient.database.writers.EventWriter;
import com.thebluealliance.androidclient.database.writers.MatchListWriter;
import com.thebluealliance.androidclient.database.writers.MatchWriter;
import com.thebluealliance.androidclient.database.writers.MediaListWriter;
import com.thebluealliance.androidclient.database.writers.MediaWriter;
import com.thebluealliance.androidclient.database.writers.TeamListWriter;
import com.thebluealliance.androidclient.database.writers.TeamWriter;
import com.thebluealliance.androidclient.database.writers.YearsParticipatedWriter;

import org.mockito.Mockito;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(includes = MockTbaAndroidModule.class)
public class MockDatabaseWriterModule {
    @Provides @Singleton
    public AwardListWriter awardListWriter(Database db) {
        return Mockito.mock(AwardListWriter.class);
    }

    @Provides @Singleton
    public AwardWriter awardWriter(Database db) {
        return Mockito.mock(AwardWriter.class);
    }

    @Provides @Singleton
    public DistrictListWriter districtListWriter(Database db) {
        return Mockito.mock(DistrictListWriter.class);
    }

    @Provides @Singleton
    public DistrictTeamListWriter districtTeamListWriter(Database db) {
        return Mockito.mock(DistrictTeamListWriter.class);
    }

    @Provides @Singleton
    public DistrictTeamWriter districtTeamWriter(Database db) {
        return Mockito.mock(DistrictTeamWriter.class);
    }

    @Provides @Singleton
    public DistrictWriter districtWriter(Database db) {
        return Mockito.mock(DistrictWriter.class);
    }

    @Provides @Singleton
    public EventListWriter eventListWriter(Database db) {
        return Mockito.mock(EventListWriter.class);
    }

    @Provides @Singleton
    public EventTeamListWriter eventTeamListWriter(Database db) {
        return Mockito.mock(EventTeamListWriter.class);
    }

    @Provides @Singleton
    public EventTeamWriter eventTeamWriter(Database db) {
        return Mockito.mock(EventTeamWriter.class);
    }

    @Provides @Singleton
    public EventWriter eventWriter(Database db) {
        return Mockito.mock(EventWriter.class);
    }

    @Provides @Singleton
    public MatchListWriter matchListWriter(Database db) {
        return Mockito.mock(MatchListWriter.class);
    }

    @Provides @Singleton
    public MatchWriter matchWriter(Database db) {
        return Mockito.mock(MatchWriter.class);
    }

    @Provides @Singleton
    public MediaListWriter mediaListWriter(Database db) {
        return Mockito.mock(MediaListWriter.class);
    }

    @Provides @Singleton
    public MediaWriter mediaWriter(Database db) {
        return Mockito.mock(MediaWriter.class);
    }

    @Provides @Singleton
    public TeamListWriter teamListWriter(Database db) {
        return Mockito.mock(TeamListWriter.class);
    }

    @Provides @Singleton
    public TeamWriter teamWriter(Database db) {
        return Mockito.mock(TeamWriter.class);
    }

    @Provides @Singleton
    public YearsParticipatedWriter yearsParticipatedWriter(Database db, TeamWriter teamWriter) {
        return Mockito.mock(YearsParticipatedWriter.class);
    }

    @Provides @Singleton
    public EventTeamAndTeamListWriter provideEventTeamAndTeamListWriter(
            Database database,
            EventTeamListWriter eventTeamListWriter,
            TeamListWriter teamListWriter) {
        return Mockito.mock(EventTeamAndTeamListWriter.class);
    }

    @Provides @Singleton
    public EventRankingsWriter provideEventRankingsWriter(Database db, EventWriter eventWriter) {
        return Mockito.mock(EventRankingsWriter.class);
    }

    @Provides @Singleton
    public EventStatsWriter provideEventStatsWriter(Database db, EventWriter eventWriter) {
        return Mockito.mock(EventStatsWriter.class);
    }

    @Provides @Singleton
    public EventDistrictPointsWriter provideEventDistrictPointsWriter(
            Database db,
            EventWriter eventWriter) {
        return Mockito.mock(EventDistrictPointsWriter.class);
    }
}
