package com.thebluealliance.androidclient.di;

import static org.mockito.Mockito.spy;

import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.database.DatabaseWriter;
import com.thebluealliance.androidclient.database.writers.AwardListWriter;
import com.thebluealliance.androidclient.database.writers.AwardWriter;
import com.thebluealliance.androidclient.database.writers.DatabaseWriterModule;
import com.thebluealliance.androidclient.database.writers.DistrictListWriter;
import com.thebluealliance.androidclient.database.writers.DistrictTeamListWriter;
import com.thebluealliance.androidclient.database.writers.DistrictTeamWriter;
import com.thebluealliance.androidclient.database.writers.DistrictWriter;
import com.thebluealliance.androidclient.database.writers.EventDetailWriter;
import com.thebluealliance.androidclient.database.writers.EventListWriter;
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

import dagger.Lazy;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.components.SingletonComponent;
import dagger.hilt.testing.TestInstallIn;

@TestInstallIn(components = SingletonComponent.class, replaces = DatabaseWriterModule.class)
@Module
public class MockDatabaseWriterModule {
    @Provides @Singleton
    public AwardListWriter awardListWriter() {
        return Mockito.mock(AwardListWriter.class);
    }

    @Provides @Singleton
    public AwardWriter awardWriter() {
        return Mockito.mock(AwardWriter.class);
    }

    @Provides @Singleton
    public DistrictListWriter districtListWriter() {
        return Mockito.mock(DistrictListWriter.class);
    }

    @Provides @Singleton
    public DistrictTeamListWriter districtTeamListWriter() {
        return Mockito.mock(DistrictTeamListWriter.class);
    }

    @Provides @Singleton
    public DistrictTeamWriter districtTeamWriter() {
        return Mockito.mock(DistrictTeamWriter.class);
    }

    @Provides @Singleton
    public DistrictWriter districtWriter() {
        return Mockito.mock(DistrictWriter.class);
    }

    @Provides @Singleton
    public EventListWriter eventListWriter() {
        return Mockito.mock(EventListWriter.class);
    }

    @Provides @Singleton
    public EventTeamListWriter eventTeamListWriter() {
        return Mockito.mock(EventTeamListWriter.class);
    }

    @Provides @Singleton
    public EventTeamWriter eventTeamWriter() {
        return Mockito.mock(EventTeamWriter.class);
    }

    @Provides @Singleton
    public EventWriter eventWriter() {
        return Mockito.mock(EventWriter.class);
    }

    @Provides @Singleton
    public MatchListWriter matchListWriter() {
        return Mockito.mock(MatchListWriter.class);
    }

    @Provides @Singleton
    public MatchWriter matchWriter() {
        return Mockito.mock(MatchWriter.class);
    }

    @Provides @Singleton
    public MediaListWriter mediaListWriter() {
        return Mockito.mock(MediaListWriter.class);
    }

    @Provides @Singleton
    public MediaWriter mediaWriter() {
        return Mockito.mock(MediaWriter.class);
    }

    @Provides @Singleton
    public TeamListWriter teamListWriter() {
        return Mockito.mock(TeamListWriter.class);
    }

    @Provides @Singleton
    public TeamWriter teamWriter() {
        return Mockito.mock(TeamWriter.class);
    }

    @Provides @Singleton
    public YearsParticipatedWriter yearsParticipatedWriter() {
        return Mockito.mock(YearsParticipatedWriter.class);
    }

    @Provides @Singleton
    public EventTeamAndTeamListWriter provideEventTeamAndTeamListWriter() {
        return Mockito.mock(EventTeamAndTeamListWriter.class);
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
        return spy(new DatabaseWriter(award, awardList, district, districtList, districtTeam,
                districtTeamList, event, eventList, eventTeam, eventTeamList, match, matchList, media,
                mediaList, team, teamList, yearsParticipated, eventTeamAndTeamList, eventDetail));
    }
}
