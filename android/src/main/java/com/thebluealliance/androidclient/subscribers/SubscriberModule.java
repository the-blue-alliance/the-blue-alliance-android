package com.thebluealliance.androidclient.subscribers;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;

import com.google.gson.Gson;
import com.thebluealliance.androidclient.config.AppConfig;
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

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ActivityComponent;
import dagger.hilt.android.components.ActivityRetainedComponent;
import dagger.hilt.android.qualifiers.ActivityContext;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.migration.DisableInstallInCheck;

/**
 * Module that injects {@link BaseAPISubscriber} objects to bind datafeed values to views
 * Each of these are annotated as @Singleton, so references are shared within their component
 * (e.g. unique references per activity)
 */
@InstallIn(ActivityComponent.class)
@Module
public class SubscriberModule {

    @Provides
    public TeamInfoSubscriber provideTeamInfoSubscriber(@ActivityContext Context context, AppConfig config) {
        return new TeamInfoSubscriber(context.getApplicationContext(), config);
    }

    @Provides
    public EventListSubscriber provideEventListRecyclerSubscriber(@ActivityContext Context context) {
        return new EventListSubscriber(context);
    }

    @Provides
    public MediaListSubscriber provideMediaListSubscriber(@ActivityContext  Context context, EventBus eventBus) {
        return new MediaListSubscriber(context.getResources(), eventBus);
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
    public RankingsListSubscriber provideRankingsListRecyclerSubscriber(Database db,
                                                                        EventBus eventBus,
                                                                        Resources resources) {
        return new RankingsListSubscriber(db, eventBus, resources);
    }

    @Provides
    public MatchListSubscriber provideMatchListSubscriber(@ActivityContext Context context, Database db, EventBus eventBus) {
        return new MatchListSubscriber(context.getResources(), db, eventBus);
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
    public StatsListSubscriber provideStatsListSubscriber(@ActivityContext Context context, Database db, EventBus eventBus) {
        return new StatsListSubscriber(context.getResources(), db, eventBus);
    }

    @Provides
    public AwardsListSubscriber provideAwardsListSubscriber(Database db, AwardRenderer renderer) {
        return new AwardsListSubscriber(db, renderer);
    }

    @Provides
    public TeamStatsSubscriber provideTeamStatsSubscriber(@ActivityContext Context context) {
        return new TeamStatsSubscriber(context.getResources());
    }

    @Provides
    public TeamAtEventSummarySubscriber provideTeamAtEventSummarySubscriber(@ApplicationContext Context context,
                                                                            MatchRenderer renderer,
                                                                            AppConfig config,
                                                                            EventBus bus) {
        return new TeamAtEventSummarySubscriber(context,
                                                config,
                                                bus,
                                                renderer);
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
    public TeamAtDistrictSummarySubscriber provideTeamAtDistrictSummarySubscriber(@ActivityContext Context context, Database db, EventBus eventBus) {
        return new TeamAtDistrictSummarySubscriber(db, context.getResources(), eventBus);
    }

    @Provides
    public TeamAtDistrictBreakdownSubscriber provideTeamAtDistrictBreakdownSubscriber(@ActivityContext Context context, Database db, Gson gson) {
        return new TeamAtDistrictBreakdownSubscriber(context.getResources(), db, gson);
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

    @Provides RecentNotificationsSubscriber provideRecentNotificationsSubscriber(@ActivityContext Context context, DatabaseWriter writer, MatchRenderer matchRenderer, Gson gson) {
        return new RecentNotificationsSubscriber(writer, context, matchRenderer, gson);
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
    public MatchBreakdownSubscriber provideMatchBreakdownSubscriber(Gson gson, AppConfig config) {
        return new MatchBreakdownSubscriber(gson, config);
    }
}
