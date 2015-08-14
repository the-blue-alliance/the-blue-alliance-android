package com.thebluealliance.androidclient.modules;

import android.app.Activity;

import com.google.gson.Gson;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.subscribers.AllianceListSubscriber;
import com.thebluealliance.androidclient.subscribers.AwardsListSubscriber;
import com.thebluealliance.androidclient.subscribers.BaseAPISubscriber;
import com.thebluealliance.androidclient.subscribers.DistrictListSubscriber;
import com.thebluealliance.androidclient.subscribers.DistrictPointsListSubscriber;
import com.thebluealliance.androidclient.subscribers.DistrictRankingsSubscriber;
import com.thebluealliance.androidclient.subscribers.EventInfoSubscriber;
import com.thebluealliance.androidclient.subscribers.EventListSubscriber;
import com.thebluealliance.androidclient.subscribers.EventTabSubscriber;
import com.thebluealliance.androidclient.subscribers.FavoriteListSubscriber;
import com.thebluealliance.androidclient.subscribers.MatchInfoSubscriber;
import com.thebluealliance.androidclient.subscribers.MatchListSubscriber;
import com.thebluealliance.androidclient.subscribers.MediaListSubscriber;
import com.thebluealliance.androidclient.subscribers.RankingsListSubscriber;
import com.thebluealliance.androidclient.subscribers.RecentNotificationsSubscriber;
import com.thebluealliance.androidclient.subscribers.StatsListSubscriber;
import com.thebluealliance.androidclient.subscribers.SubscriptionListSubscriber;
import com.thebluealliance.androidclient.subscribers.TeamAtDistrictBreakdownSubscriber;
import com.thebluealliance.androidclient.subscribers.TeamAtDistrictSummarySubscriber;
import com.thebluealliance.androidclient.subscribers.TeamAtEventSummarySubscriber;
import com.thebluealliance.androidclient.subscribers.TeamInfoSubscriber;
import com.thebluealliance.androidclient.subscribers.TeamListSubscriber;
import com.thebluealliance.androidclient.subscribers.TeamStatsSubscriber;
import com.thebluealliance.androidclient.subscribers.WebcastListSubscriber;

import dagger.Module;
import dagger.Provides;
import de.greenrobot.event.EventBus;

/**
 * Module that injects {@link BaseAPISubscriber} objects to bind datafeed values to views
 * Each of these are annotated as @Singleton, so references are shared within their component
 * (e.g. unique references per activity)
 */
@Module(includes = {TBAAndroidModule.class})
public class SubscriberModule {

    private Activity mActivity;

    public SubscriberModule(Activity activity) {
        mActivity = activity;
    }

    @Provides
    public TeamInfoSubscriber provideTeamInfoSubscriber() {
        return new TeamInfoSubscriber();
    }

    @Provides
    public EventListSubscriber provideEventListSubscriber() {
        return new EventListSubscriber();
    }

    @Provides
    public MediaListSubscriber provideMediaListSubscriber() {
        return new MediaListSubscriber(mActivity.getResources());
    }

    @Provides
    public EventInfoSubscriber provideEventInfoSubscriber() {
        return new EventInfoSubscriber();
    }

    @Provides
    public TeamListSubscriber provideTeamListSubscriber() {
        return new TeamListSubscriber();
    }

    @Provides
    public RankingsListSubscriber provideRankingsListSubscriber(Database db, EventBus eventBus) {
        return new RankingsListSubscriber(db, eventBus);
    }

    @Provides
    public MatchListSubscriber provideMatchListSubscriber(Database db, EventBus eventBus) {
        return new MatchListSubscriber(mActivity.getResources(), db, eventBus);
    }

    @Provides
    public AllianceListSubscriber provideAllianceListSubscriber() {
        return new AllianceListSubscriber();
    }

    @Provides
    public DistrictPointsListSubscriber provideDistrictPointsListSubscriber(
      Database db,
      Gson gson) {
        return new DistrictPointsListSubscriber(db, gson);
    }

    @Provides
    public StatsListSubscriber provideStatsListSubscriber(Database db, EventBus eventBus) {
        return new StatsListSubscriber(mActivity.getResources(), db, eventBus);
    }

    @Provides
    public AwardsListSubscriber provideAwardsListSubscriber(Database db) {
        return new AwardsListSubscriber(db);
    }

    @Provides TeamStatsSubscriber provideTeamStatsSubscriber() {
        return new TeamStatsSubscriber(mActivity);
    }

    @Provides TeamAtEventSummarySubscriber provideTeamAtEventSummarySubscriber(Database db) {
        return new TeamAtEventSummarySubscriber(mActivity, db);
    }

    @Provides EventTabSubscriber provideEventTabsSubscriber() {
        return new EventTabSubscriber();
    }

    @Provides DistrictListSubscriber provideDistrictListSubscriber(Database db) {
        return new DistrictListSubscriber(db);
    }

    @Provides DistrictRankingsSubscriber provideDistrictRankingsSubscriber(Database db) {
        return new DistrictRankingsSubscriber(db);
    }

    @Provides TeamAtDistrictSummarySubscriber provideTeamAtDistrictSummarySubscriber(
      Database db,
      EventBus eventBus) {
        return new TeamAtDistrictSummarySubscriber(db, mActivity.getResources(), eventBus);
    }

    @Provides TeamAtDistrictBreakdownSubscriber provideTeamAtDistrictBreakdownSubscriber(
      Database db,
      Gson gson) {
        return new TeamAtDistrictBreakdownSubscriber(mActivity.getResources(), db, gson);
    }

    @Provides MatchInfoSubscriber provideMatchInfoSubscriber(Gson gson, EventBus eventBus) {
        return new MatchInfoSubscriber(gson, eventBus);
    }

    @Provides WebcastListSubscriber provideWebcastListSubscriber() {
        return new WebcastListSubscriber();
    }

    @Provides
    RecentNotificationsSubscriber provideRecentNotificationsSubscriber() {
        return new RecentNotificationsSubscriber();
    }

    @Provides SubscriptionListSubscriber provideSubscriptionListSubscriber() {
        return new SubscriptionListSubscriber(mActivity);
    }

    @Provides FavoriteListSubscriber provideFavoriteListSubscriber() {
        return new FavoriteListSubscriber(mActivity);
    }
}
