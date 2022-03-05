package com.thebluealliance.androidclient.di;

import com.thebluealliance.androidclient.fragments.framework.SimpleSubscriber;
import com.thebluealliance.androidclient.subscribers.AllianceListSubscriber;
import com.thebluealliance.androidclient.subscribers.AwardsListSubscriber;
import com.thebluealliance.androidclient.subscribers.ContributorListSubscriber;
import com.thebluealliance.androidclient.subscribers.DistrictListSubscriber;
import com.thebluealliance.androidclient.subscribers.DistrictPointsListSubscriber;
import com.thebluealliance.androidclient.subscribers.DistrictRankingsSubscriber;
import com.thebluealliance.androidclient.subscribers.EventInfoSubscriber;
import com.thebluealliance.androidclient.subscribers.EventListSubscriber;
import com.thebluealliance.androidclient.subscribers.EventTabSubscriber;
import com.thebluealliance.androidclient.subscribers.FavoriteListSubscriber;
import com.thebluealliance.androidclient.subscribers.MatchBreakdownSubscriber;
import com.thebluealliance.androidclient.subscribers.MatchInfoSubscriber;
import com.thebluealliance.androidclient.subscribers.MatchListSubscriber;
import com.thebluealliance.androidclient.subscribers.MediaListSubscriber;
import com.thebluealliance.androidclient.subscribers.RankingsListSubscriber;
import com.thebluealliance.androidclient.subscribers.RecentNotificationsSubscriber;
import com.thebluealliance.androidclient.subscribers.StatsListSubscriber;
import com.thebluealliance.androidclient.subscribers.SubscriberModule;
import com.thebluealliance.androidclient.subscribers.SubscriptionListSubscriber;
import com.thebluealliance.androidclient.subscribers.TeamAtDistrictBreakdownSubscriber;
import com.thebluealliance.androidclient.subscribers.TeamAtDistrictSummarySubscriber;
import com.thebluealliance.androidclient.subscribers.TeamAtEventSummarySubscriber;
import com.thebluealliance.androidclient.subscribers.TeamInfoSubscriber;
import com.thebluealliance.androidclient.subscribers.TeamListSubscriber;
import com.thebluealliance.androidclient.subscribers.TeamStatsSubscriber;
import com.thebluealliance.androidclient.subscribers.WebcastListSubscriber;

import org.mockito.Mockito;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.android.components.ActivityComponent;
import dagger.hilt.testing.TestInstallIn;

@TestInstallIn(components = ActivityComponent.class, replaces = SubscriberModule.class)
@Module
public class MockSubscriberModule {
    @Provides
    public TeamInfoSubscriber provideTeamInfoSubscriber() {
        return Mockito.mock(TeamInfoSubscriber.class);
    }

    @Provides
    public EventListSubscriber providesEventListRecyclerSubscriber() {
        return Mockito.mock(EventListSubscriber.class);
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
    public TeamListSubscriber provideTeamListSubscriber() {
        return Mockito.mock(TeamListSubscriber.class);
    }

    @Provides
    public RankingsListSubscriber provideRankingsListRecyclerSubscriber() {
        return Mockito.mock(RankingsListSubscriber.class);
    }

    @Provides
    public MatchListSubscriber provideMatchListSubscriber() {
        return Mockito.mock(MatchListSubscriber.class);
    }

    @Provides
    public AllianceListSubscriber provideAllianceListSubscriber() {
        return Mockito.mock(AllianceListSubscriber.class);
    }

    @Provides
    public DistrictPointsListSubscriber provideDistrictPointsListSubscriber() {
        return Mockito.mock(DistrictPointsListSubscriber.class);
    }

    @Provides
    public StatsListSubscriber provideStatsListSubscriber() {
        return Mockito.mock(StatsListSubscriber.class);
    }

    @Provides
    public AwardsListSubscriber provideAwardsListSubscriber() {
        return Mockito.mock(AwardsListSubscriber.class);
    }

    @Provides
    public TeamStatsSubscriber provideTeamStatsSubscriber() {
        return Mockito.mock(TeamStatsSubscriber.class);
    }

    @Provides
    public TeamAtEventSummarySubscriber provideTeamAtEventSummarySubscriber() {
        return Mockito.mock(TeamAtEventSummarySubscriber.class);
    }

    @Provides
    public EventTabSubscriber provideEventTabsSubscriber() {
        return Mockito.mock(EventTabSubscriber.class);
    }

    @Provides
    public DistrictListSubscriber provideDistrictListSubscriber() {
        return Mockito.mock(DistrictListSubscriber.class);
    }

    @Provides
    public DistrictRankingsSubscriber provideDistrictRankingsSubscriber() {
        return Mockito.mock(DistrictRankingsSubscriber.class);
    }

    @Provides
    public TeamAtDistrictSummarySubscriber provideTeamAtDistrictSummarySubscriber() {
        return Mockito.mock(TeamAtDistrictSummarySubscriber.class);
    }

    @Provides
    public TeamAtDistrictBreakdownSubscriber provideTeamAtDistrictBreakdownSubscriber() {
        return Mockito.mock(TeamAtDistrictBreakdownSubscriber.class);
    }

    @Provides
    public MatchInfoSubscriber provideMatchInfoSubscriber() {
        return Mockito.mock(MatchInfoSubscriber.class);
    }

    @Provides
    public WebcastListSubscriber provideWebcastListSubscriber() {
        return Mockito.mock(WebcastListSubscriber.class);
    }

    @Provides
    public RecentNotificationsSubscriber provideRecentNotificationsSubscriber() {
        return Mockito.mock(RecentNotificationsSubscriber.class);
    }

    @Provides
    public SubscriptionListSubscriber provideSubscriptionListSubscriber() {
        return Mockito.mock(SubscriptionListSubscriber.class);
    }

    @Provides
    public FavoriteListSubscriber provideFavoriteListSubscriber() {
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

    @Provides
    public SimpleSubscriber provideBaseSubscriber() {
        return Mockito.mock(SimpleSubscriber.class);
    }
}
