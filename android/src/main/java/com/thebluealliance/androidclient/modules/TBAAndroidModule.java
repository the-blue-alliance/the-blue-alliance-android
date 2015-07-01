package com.thebluealliance.androidclient.modules;

import android.content.Context;

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
import com.thebluealliance.androidclient.database.writers.EventTeamListWriter;
import com.thebluealliance.androidclient.database.writers.EventTeamWriter;
import com.thebluealliance.androidclient.database.writers.EventWriter;
import com.thebluealliance.androidclient.database.writers.MatchListWriter;
import com.thebluealliance.androidclient.database.writers.MatchWriter;
import com.thebluealliance.androidclient.database.writers.MediaListWriter;
import com.thebluealliance.androidclient.database.writers.MediaWriter;
import com.thebluealliance.androidclient.database.writers.TeamListWriter;
import com.thebluealliance.androidclient.database.writers.TeamWriter;

import javax.inject.Singleton;

import dagger.Lazy;
import dagger.Module;
import dagger.Provides;

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
      Lazy<TeamListWriter> teamList) {
        return new DatabaseWriter(award, awardList, district, districtList, districtTeam,
          districtTeamList, event, eventList, eventTeam, eventTeamList, match, matchList, media,
          mediaList, team, teamList);
    }
}
