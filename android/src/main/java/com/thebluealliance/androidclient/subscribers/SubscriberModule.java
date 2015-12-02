package com.thebluealliance.androidclient.subscribers;

import android.app.Activity;

import com.google.gson.Gson;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.di.TBAAndroidModule;
import com.thebluealliance.androidclient.renderers.AwardRenderer;
import com.thebluealliance.androidclient.renderers.DistrictRenderer;
import com.thebluealliance.androidclient.renderers.EventRenderer;
import com.thebluealliance.androidclient.renderers.MatchRenderer;
import com.thebluealliance.androidclient.renderers.MyTbaModelRenderer;
import com.thebluealliance.androidclient.renderers.RendererModule;
import com.thebluealliance.androidclient.renderers.TeamRenderer;

import dagger.Module;
import dagger.Provides;
import de.greenrobot.event.EventBus;

/**
 * Module that injects {@link BaseAPISubscriber} objects to bind datafeed values to views
 * Each of these are annotated as @Singleton, so references are shared within their component
 * (e.g. unique references per activity)
 */
@Module(includes = {TBAAndroidModule.class, RendererModule.class})
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
    public EventListSubscriber provideEventListSubscriber(EventRenderer renderer) {
        return new EventListSubscriber(renderer);
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
    public TeamListSubscriber provideTeamListSubscriber(TeamRenderer renderer) {
        return new TeamListSubscriber(renderer);
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
    public AllianceListSubscriber provideAllianceListSubscriber(EventRenderer renderer) {
        return new AllianceListSubscriber(renderer);
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
    public AwardsListSubscriber provideAwardsListSubscriber(Database db, AwardRenderer renderer) {
        return new AwardsListSubscriber(db, renderer);
    }

    @Provides TeamStatsSubscriber provideTeamStatsSubscriber() {
        return new TeamStatsSubscriber(mActivity.getResources());
    }

    @Provides
    TeamAtEventSummarySubscriber provideTeamAtEventSummarySubscriber(MatchRenderer renderer) {
        return new TeamAtEventSummarySubscriber(mActivity.getResources(), renderer);
    }

    @Provides EventTabSubscriber provideEventTabsSubscriber() {
        return new EventTabSubscriber();
    }

    @Provides
    DistrictListSubscriber provideDistrictListSubscriber(Database db, DistrictRenderer renderer) {
        return new DistrictListSubscriber(db, renderer);
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

    @Provides
    MatchInfoSubscriber provideMatchInfoSubscriber(
      Gson gson,
      EventBus eventBus,
      MatchRenderer renderer) {
        return new MatchInfoSubscriber(gson, eventBus, renderer);
    }

    @Provides WebcastListSubscriber provideWebcastListSubscriber(EventRenderer renderer) {
        return new WebcastListSubscriber(renderer);
    }

    @Provides
    RecentNotificationsSubscriber provideRecentNotificationsSubscriber() {
        return new RecentNotificationsSubscriber();
    }

    @Provides
    SubscriptionListSubscriber provideSubscriptionListSubscriber(MyTbaModelRenderer renderer) {
        return new SubscriptionListSubscriber(renderer);
    }

    @Provides
    FavoriteListSubscriber provideFavoriteListSubscriber(MyTbaModelRenderer renderer) {
        return new FavoriteListSubscriber(renderer);
    }
}
