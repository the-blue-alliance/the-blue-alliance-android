package com.thebluealliance.androidclient.modules;

import android.app.Activity;

import com.google.gson.Gson;
import com.thebluealliance.androidclient.TBAAndroid;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.modules.components.DaggerApplicationComponent;
import com.thebluealliance.androidclient.subscribers.AllianceListSubscriber;
import com.thebluealliance.androidclient.subscribers.DistrictPointsListSubscriber;
import com.thebluealliance.androidclient.subscribers.EventInfoSubscriber;
import com.thebluealliance.androidclient.subscribers.EventListSubscriber;
import com.thebluealliance.androidclient.subscribers.MatchListSubscriber;
import com.thebluealliance.androidclient.subscribers.MediaListSubscriber;
import com.thebluealliance.androidclient.subscribers.RankingsListSubscriber;
import com.thebluealliance.androidclient.subscribers.StatsListSubscriber;
import com.thebluealliance.androidclient.subscribers.TeamInfoSubscriber;
import com.thebluealliance.androidclient.subscribers.TeamListSubscriber;

import javax.inject.Inject;

import dagger.Module;
import dagger.Provides;

@Module(includes = TBAAndroidModule.class)
public class SubscriberModule {

    private Activity mActivity;
    @Inject Database mDb;
    @Inject Gson mGson;

    public SubscriberModule(Activity activity) {
        mActivity = activity;
        TBAAndroid application = ((TBAAndroid) activity.getApplication());
        DaggerApplicationComponent.builder()
          .tBAAndroidModule(application.getModule())
          .datafeedModule(application.getDatafeedModule())
          .build()
          .inject(this);
    }

    @Provides
    public TeamInfoSubscriber provideTeamInfoSubscriber() {
        return new TeamInfoSubscriber();
    }

    @Provides
    public EventListSubscriber provideEventListSubscriber() {
        return new EventListSubscriber(mActivity);
    }

    @Provides
    public MediaListSubscriber provideMediaListSubscriber() {
        return new MediaListSubscriber(mActivity);
    }

    @Provides
    public EventInfoSubscriber provideEventInfoSubscriber() {
        return new EventInfoSubscriber();
    }

    @Provides
    public TeamListSubscriber provideTeamListSubscriber() {
        return new TeamListSubscriber(mActivity);
    }

    @Provides
    public RankingsListSubscriber provideRankingsListSubscriber() {
        return new RankingsListSubscriber(mActivity, mDb);
    }

    @Provides
    public MatchListSubscriber provideMatchListSubscriber() {
        return new MatchListSubscriber(mActivity, mDb);
    }

    @Provides
    public AllianceListSubscriber provideAllianceListSubscriber() {
        return new AllianceListSubscriber(mActivity);
    }

    @Provides
    public DistrictPointsListSubscriber provideDistrictPointsListSubscriber() {
        return new DistrictPointsListSubscriber(mActivity, mDb, mGson);
    }

    @Provides
    public StatsListSubscriber provideStatsListSubscriber() {
        return new StatsListSubscriber(mActivity, mDb);
    }
}
