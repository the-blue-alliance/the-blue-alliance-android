package com.thebluealliance.androidclient.di;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.thebluealliance.androidclient.TBAAndroid;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.database.DatabaseWriter;
import com.thebluealliance.androidclient.database.writers.AwardListWriter;
import com.thebluealliance.androidclient.database.writers.AwardWriter;
import com.thebluealliance.androidclient.database.writers.DistrictListWriter;
import com.thebluealliance.androidclient.database.writers.DistrictTeamListWriter;
import com.thebluealliance.androidclient.database.writers.DistrictTeamWriter;
import com.thebluealliance.androidclient.database.writers.DistrictWriter;
import com.thebluealliance.androidclient.database.writers.EventListWriter;
import com.thebluealliance.androidclient.database.writers.EventRankingsWriter;
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

import javax.inject.Singleton;

import dagger.Lazy;
import dagger.Module;
import dagger.Provides;
import de.greenrobot.event.EventBus;

/**
 * App-wide dependency injection items
 */
@Module
public class TBAAndroidModule {
    static TBAAndroid mApp;

    public TBAAndroidModule() {
    }

    public TBAAndroidModule(TBAAndroid app) {
        mApp = app;
    }

    @Provides
    public Context provideApplicationContext() {
        return mApp.getApplicationContext();
    }

    @Provides
    @Singleton
    public Database provideDatabase() {
        return Database.getInstance(mApp);
    }

    @Provides
    @Singleton
    public SharedPreferences provideSharedPrefs(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Provides
    @Singleton
    public EventBus provideEventBus() {
        return EventBus.getDefault();
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
      Lazy<EventRankingsWriter> eventRankings) {
        return new DatabaseWriter(award, awardList, district, districtList, districtTeam,
          districtTeamList, event, eventList, eventTeam, eventTeamList, match, matchList, media,
          mediaList, team, teamList, yearsParticipated, eventTeamAndTeamList, eventRankings);
    }
}
