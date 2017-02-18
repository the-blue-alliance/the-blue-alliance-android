package com.thebluealliance.androidclient.subscribers;

import com.google.gson.Gson;

import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.database.DatabaseWriter;
import com.thebluealliance.androidclient.di.TBAAndroidModule;
import com.thebluealliance.androidclient.renderers.AwardRenderer;
import com.thebluealliance.androidclient.renderers.DistrictPointBreakdownRenderer;
import com.thebluealliance.androidclient.renderers.DistrictRenderer;
import com.thebluealliance.androidclient.renderers.EventRenderer;
import com.thebluealliance.androidclient.renderers.MatchRenderer;
import com.thebluealliance.androidclient.renderers.MediaRenderer;
import com.thebluealliance.androidclient.renderers.MyTbaModelRenderer;
import com.thebluealliance.androidclient.renderers.RendererModule;
import com.thebluealliance.androidclient.renderers.TeamRenderer;

import org.greenrobot.eventbus.EventBus;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;

import dagger.Module;
import dagger.Provides;

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
    public EventListSubscriber provideEventListRecyclerSubscriber(Context context) {
        return new EventListSubscriber(context);
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

    @Provides RankingsListSubscriber provideRankingsListRecyclerSubscriber(Database db, EventBus eventBus) {
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
      Gson gson,
      DistrictPointBreakdownRenderer renderer) {
        return new DistrictPointsListSubscriber(db, gson, renderer);
    }

    @Provides
    public StatsListSubscriber provideStatsListSubscriber(Database db, EventBus eventBus) {
        return new StatsListSubscriber(mActivity.getResources(), db, eventBus);
    }

    @Provides
    public AwardsListSubscriber provideAwardsListSubscriber(Database db, AwardRenderer renderer) {
        return new AwardsListSubscriber(db, renderer);
    }

    @Provides
    public TeamStatsSubscriber provideTeamStatsSubscriber() {
        return new TeamStatsSubscriber(mActivity.getResources());
    }

    @Provides
    public TeamAtEventSummarySubscriber provideTeamAtEventSummarySubscriber(MatchRenderer renderer) {
        return new TeamAtEventSummarySubscriber(mActivity, renderer);
    }

    @Provides
    public EventTabSubscriber provideEventTabsSubscriber() {
        return new EventTabSubscriber();
    }

    @Provides
    public DistrictListSubscriber provideDistrictListSubscriber(Database db, DistrictRenderer renderer) {
        return new DistrictListSubscriber(db, renderer);
    }

    @Provides
    public DistrictRankingsSubscriber provideDistrictRankingsSubscriber(Database db) {
        return new DistrictRankingsSubscriber(db);
    }

    @Provides
    public TeamAtDistrictSummarySubscriber provideTeamAtDistrictSummarySubscriber(
      Database db,
      EventBus eventBus) {
        return new TeamAtDistrictSummarySubscriber(db, mActivity.getResources(), eventBus);
    }

    @Provides
    public TeamAtDistrictBreakdownSubscriber provideTeamAtDistrictBreakdownSubscriber(
      Database db,
      Gson gson) {
        return new TeamAtDistrictBreakdownSubscriber(mActivity.getResources(), db, gson);
    }

    @Provides
    MatchInfoSubscriber provideMatchInfoSubscriber(
            Gson gson,
            EventBus eventBus,
            MatchRenderer renderer,
            MediaRenderer mediaRenderer,
            Resources resources) {
        return new MatchInfoSubscriber(gson, eventBus, renderer, mediaRenderer, resources);
    }

    @Provides
    public WebcastListSubscriber provideWebcastListSubscriber(EventRenderer renderer) {
        return new WebcastListSubscriber(renderer);
    }

    @Provides RecentNotificationsSubscriber provideRecentNotificationsSubscriber(DatabaseWriter writer, MatchRenderer matchRenderer) {
        return new RecentNotificationsSubscriber(writer, mActivity, matchRenderer);
    }

    @Provides
    public SubscriptionListSubscriber provideSubscriptionListSubscriber(MyTbaModelRenderer renderer) {
        return new SubscriptionListSubscriber(renderer);
    }

    @Provides
    public FavoriteListSubscriber provideFavoriteListSubscriber(MyTbaModelRenderer renderer) {
        return new FavoriteListSubscriber(renderer);
    }

    @Provides
    public ContributorListSubscriber provideContributorListSubscriber() {
        return new ContributorListSubscriber();
    }

    @Provides
    public MatchBreakdownSubscriber provideMatchBreakdownSubscriber(Gson gson) {
        return new MatchBreakdownSubscriber(gson);
    }
}
