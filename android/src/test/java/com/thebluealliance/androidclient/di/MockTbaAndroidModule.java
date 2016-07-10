package com.thebluealliance.androidclient.di;

import com.google.android.gms.analytics.Tracker;

import com.squareup.sqlbrite.BriteDatabase;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.database.DatabaseWriter;
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

import org.greenrobot.eventbus.EventBus;
import org.mockito.Mockito;

import android.content.Context;
import android.content.SharedPreferences;

import javax.inject.Singleton;

import dagger.Lazy;
import dagger.Module;
import dagger.Provides;

import static org.mockito.Mockito.spy;

@Module
public class MockTbaAndroidModule  {

    @Provides
    @Singleton
    public Context provideApplicationContext() {
        return Mockito.mock(Context.class);
    }

    @Provides
    @Singleton
    public Database provideDatabase() {
        return Mockito.mock(Database.class);
    }

    @Provides
    @Singleton
    public BriteDatabase provideBriteDatabase() {
        return Mockito.mock(BriteDatabase.class);
    }

    @Provides
    @Singleton
    public SharedPreferences provideSharedPrefs(Context context) {
        return Mockito.mock(SharedPreferences.class);
    }

    @Provides
    @Singleton
    public EventBus provideEventBus() {
        return Mockito.mock(EventBus.class);
    }

    @Provides
    @Singleton
    public Tracker provideAndroidTracker(Context context) {
        return Mockito.mock(Tracker.class);
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
            Lazy<EventRankingsWriter> eventRankings,
            Lazy<EventStatsWriter> eventStats,
            Lazy<EventDistrictPointsWriter> eventDistrictPoints) {
        return spy(new DatabaseWriter(award, awardList, district, districtList, districtTeam,
                districtTeamList, event, eventList, eventTeam, eventTeamList, match, matchList, media,
                mediaList, team, teamList, yearsParticipated, eventTeamAndTeamList, eventRankings,
                eventStats, eventDistrictPoints));
    }
}
