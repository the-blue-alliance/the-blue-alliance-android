package com.thebluealliance.androidclient.di;

import android.accounts.AccountManager;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.android.gms.analytics.Tracker;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.gson.Gson;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.database.DatabaseWithMocks;
import com.thebluealliance.androidclient.database.DatabaseWriter;
import com.thebluealliance.androidclient.database.writers.AwardListWriter;
import com.thebluealliance.androidclient.database.writers.AwardWriter;
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

import org.greenrobot.eventbus.EventBus;
import org.mockito.Mockito;

import javax.inject.Singleton;

import androidx.test.core.app.ApplicationProvider;
import dagger.Lazy;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;
import dagger.hilt.internal.TestSingletonComponent;
import dagger.hilt.testing.TestInstallIn;

import static org.mockito.Mockito.spy;

@Module
@TestInstallIn(components = SingletonComponent.class, replaces = TBAAndroidModule.class)
public class MockTbaAndroidModule {
    /*
    @Provides
    @Singleton
    public Database provideDatabase(@ApplicationContext Context context, Gson gson) {
        return spy(new DatabaseWithMocks(context, gson));
    }

    @Provides
    @Singleton
    public SharedPreferences provideSharedPrefs(@ApplicationContext Context context) {
        return spy(context.getSharedPreferences("prefs", 0));
    }

    @Provides
    @Singleton
    public EventBus provideEventBus() {
        return Mockito.mock(EventBus.class);
    }

    @Provides
    @Singleton
    public Tracker provideAndroidTracker() {
        return Mockito.mock(Tracker.class);
    }

    @Provides @Singleton
    public AccountManager provideAccountManager() {
        return Mockito.mock(AccountManager.class);
    }

    @Provides @Singleton
    public FirebaseRemoteConfig provideFirebaseRemoteConfig() {
        return Mockito.mock(FirebaseRemoteConfig.class);
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
     */
}
