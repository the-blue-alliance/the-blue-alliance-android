package com.thebluealliance.androidclient.modules;

import android.app.Activity;

import com.google.gson.Gson;
import com.thebluealliance.androidclient.TBAAndroid;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.modules.components.DaggerApplicationComponent;
import com.thebluealliance.androidclient.subscribers.AllianceListSubscriber;
import com.thebluealliance.androidclient.subscribers.AwardsListSubscriber;
import com.thebluealliance.androidclient.subscribers.BaseAPISubscriber;
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
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Module that injects {@link BaseAPISubscriber} objects to bind datafeed values to views
 * Each of these are annotated as @Singleton, so references are shared within their component
 * (e.g. unique references per activity)
 */
@Module(includes = {TBAAndroidModule.class})
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

    @Provides @Singleton
    public TeamInfoSubscriber provideTeamInfoSubscriber() {
        return new TeamInfoSubscriber();
    }

    @Provides @Singleton
    public EventListSubscriber provideEventListSubscriber() {
        return new EventListSubscriber(mActivity);
    }

    @Provides @Singleton
    public MediaListSubscriber provideMediaListSubscriber() {
        return new MediaListSubscriber(mActivity);
    }

    @Provides @Singleton
    public EventInfoSubscriber provideEventInfoSubscriber() {
        return new EventInfoSubscriber();
    }

    @Provides @Singleton
    public TeamListSubscriber provideTeamListSubscriber() {
        return new TeamListSubscriber(mActivity);
    }

    @Provides @Singleton
    public RankingsListSubscriber provideRankingsListSubscriber() {
        return new RankingsListSubscriber(mActivity, mDb);
    }

    @Provides @Singleton
    public MatchListSubscriber provideMatchListSubscriber() {
        return new MatchListSubscriber(mActivity, mDb);
    }

    @Provides @Singleton
    public AllianceListSubscriber provideAllianceListSubscriber() {
        return new AllianceListSubscriber(mActivity);
    }

    @Provides @Singleton
    public DistrictPointsListSubscriber provideDistrictPointsListSubscriber() {
        return new DistrictPointsListSubscriber(mActivity, mDb, mGson);
    }

    @Provides @Singleton
    public StatsListSubscriber provideStatsListSubscriber() {
        return new StatsListSubscriber(mActivity, mDb);
    }

    @Provides @Singleton
    public AwardsListSubscriber provideAwardsListSubscriber() {
        return new AwardsListSubscriber(mActivity, mDb);
    }
}
