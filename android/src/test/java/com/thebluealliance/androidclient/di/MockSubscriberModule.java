package com.thebluealliance.androidclient.di;

import com.google.gson.Gson;

import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.database.DatabaseWriter;
import com.thebluealliance.androidclient.fragments.framework.SimpleSubscriber;
import com.thebluealliance.androidclient.renderers.AwardRenderer;
import com.thebluealliance.androidclient.renderers.DistrictPointBreakdownRenderer;
import com.thebluealliance.androidclient.renderers.DistrictRenderer;
import com.thebluealliance.androidclient.renderers.EventRenderer;
import com.thebluealliance.androidclient.renderers.MatchRenderer;
import com.thebluealliance.androidclient.renderers.MediaRenderer;
import com.thebluealliance.androidclient.renderers.MyTbaModelRenderer;
import com.thebluealliance.androidclient.renderers.TeamRenderer;
import com.thebluealliance.androidclient.subscribers.AllianceListSubscriber;
import com.thebluealliance.androidclient.subscribers.AwardsListSubscriber;
import com.thebluealliance.androidclient.subscribers.ContributorListSubscriber;
import com.thebluealliance.androidclient.subscribers.DistrictListSubscriber;
import com.thebluealliance.androidclient.subscribers.DistrictPointsListSubscriber;
import com.thebluealliance.androidclient.subscribers.DistrictRankingsSubscriber;
import com.thebluealliance.androidclient.subscribers.EventInfoSubscriber;
import com.thebluealliance.androidclient.subscribers.EventListRecyclerSubscriber;
import com.thebluealliance.androidclient.subscribers.EventTabSubscriber;
import com.thebluealliance.androidclient.subscribers.FavoriteListSubscriber;
import com.thebluealliance.androidclient.subscribers.MatchBreakdownSubscriber;
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

import org.greenrobot.eventbus.EventBus;
import org.mockito.Mockito;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(includes = {MockTbaAndroidModule.class, MockRendererModule.class})
public class MockSubscriberModule {

    @Provides
    public TeamInfoSubscriber provideTeamInfoSubscriber() {
        return Mockito.mock(TeamInfoSubscriber.class);
    }

    @Provides
    public EventListRecyclerSubscriber providesEventListRecyclerSubscriber(Context context) {
        return Mockito.mock(EventListRecyclerSubscriber.class);
    }

    @Provides
    public MediaListSubscriber provideMediaListSubscriber() {
        return Mockito.mock(MediaListSubscriber.class);
    }

    @Provides
    public EventInfoSubscriber provideEventInfoSubscriber() {
        return Mockito.mock(EventInfoSubscriber.class);
    }

    @Provides
    public TeamListSubscriber provideTeamListSubscriber(TeamRenderer renderer) {
        return Mockito.mock(TeamListSubscriber.class);
    }

    @Provides
    public RankingsListSubscriber provideRankingsListSubscriber(Database db, EventBus eventBus) {
        return Mockito.mock(RankingsListSubscriber.class);
    }

    @Provides
    public MatchListSubscriber provideMatchListSubscriber(Database db, EventBus eventBus) {
        return Mockito.mock(MatchListSubscriber.class);
    }

    @Provides
    public AllianceListSubscriber provideAllianceListSubscriber(EventRenderer renderer) {
        return Mockito.mock(AllianceListSubscriber.class);
    }

    @Provides
    public DistrictPointsListSubscriber provideDistrictPointsListSubscriber(
            Database db,
            Gson gson,
            DistrictPointBreakdownRenderer renderer) {
        return Mockito.mock(DistrictPointsListSubscriber.class);
    }

    @Provides
    public StatsListSubscriber provideStatsListSubscriber(Database db, EventBus eventBus) {
        return Mockito.mock(StatsListSubscriber.class);
    }

    @Provides
    public AwardsListSubscriber provideAwardsListSubscriber(Database db, AwardRenderer renderer) {
        return Mockito.mock(AwardsListSubscriber.class);
    }

    @Provides
    public TeamStatsSubscriber provideTeamStatsSubscriber() {
        return Mockito.mock(TeamStatsSubscriber.class);
    }

    @Provides
    public TeamAtEventSummarySubscriber provideTeamAtEventSummarySubscriber(MatchRenderer renderer) {
        return Mockito.mock(TeamAtEventSummarySubscriber.class);
    }

    @Provides
    public EventTabSubscriber provideEventTabsSubscriber() {
        return Mockito.mock(EventTabSubscriber.class);
    }

    @Provides
    public DistrictListSubscriber provideDistrictListSubscriber(Database db, DistrictRenderer renderer) {
        return Mockito.mock(DistrictListSubscriber.class);
    }

    @Provides
    public DistrictRankingsSubscriber provideDistrictRankingsSubscriber(Database db) {
        return Mockito.mock(DistrictRankingsSubscriber.class);
    }

    @Provides
    public TeamAtDistrictSummarySubscriber provideTeamAtDistrictSummarySubscriber(
            Database db,
            EventBus eventBus) {
        return Mockito.mock(TeamAtDistrictSummarySubscriber.class);
    }

    @Provides
    public TeamAtDistrictBreakdownSubscriber provideTeamAtDistrictBreakdownSubscriber(
            Database db,
            Gson gson) {
        return Mockito.mock(TeamAtDistrictBreakdownSubscriber.class);
    }

    @Provides
    public MatchInfoSubscriber provideMatchInfoSubscriber(
            Gson gson,
            EventBus eventBus,
            MatchRenderer renderer,
            MediaRenderer mediaRenderer) {
        return Mockito.mock(MatchInfoSubscriber.class);
    }

    @Provides
    public WebcastListSubscriber provideWebcastListSubscriber(EventRenderer renderer) {
        return Mockito.mock(WebcastListSubscriber.class);
    }

    @Provides
    public RecentNotificationsSubscriber provideRecentNotificationsSubscriber(DatabaseWriter writer) {
        return Mockito.mock(RecentNotificationsSubscriber.class);
    }

    @Provides
    public SubscriptionListSubscriber provideSubscriptionListSubscriber(MyTbaModelRenderer renderer) {
        return Mockito.mock(SubscriptionListSubscriber.class);
    }

    @Provides
    public FavoriteListSubscriber provideFavoriteListSubscriber(MyTbaModelRenderer renderer) {
        return Mockito.mock(FavoriteListSubscriber.class);
    }

    @Provides
    public ContributorListSubscriber provideContributorListSubscriber() {
        return Mockito.mock(ContributorListSubscriber.class);
    }

    @Provides
    public MatchBreakdownSubscriber provideMatchBreakdownSubscriber() {
        return Mockito.mock(MatchBreakdownSubscriber.class);
    }

    @Provides @Singleton
    public SimpleSubscriber provideBaseSubscriber() {
        return Mockito.mock(SimpleSubscriber.class);
    }
}
