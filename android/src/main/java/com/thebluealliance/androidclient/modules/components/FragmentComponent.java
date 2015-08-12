package com.thebluealliance.androidclient.modules.components;

import com.thebluealliance.androidclient.datafeed.CacheableDatafeed;
import com.thebluealliance.androidclient.fragments.EventListFragment;
import com.thebluealliance.androidclient.fragments.EventsByWeekFragment;
import com.thebluealliance.androidclient.fragments.RecentNotificationsFragment;
import com.thebluealliance.androidclient.fragments.TeamListFragment;
import com.thebluealliance.androidclient.fragments.district.DistrictEventsFragment;
import com.thebluealliance.androidclient.fragments.district.DistrictListFragment;
import com.thebluealliance.androidclient.fragments.district.DistrictRankingsFragment;
import com.thebluealliance.androidclient.fragments.district.TeamAtDistrictBreakdownFragment;
import com.thebluealliance.androidclient.fragments.district.TeamAtDistrictSummaryFragment;
import com.thebluealliance.androidclient.fragments.event.EventAlliancesFragment;
import com.thebluealliance.androidclient.fragments.event.EventAwardsFragment;
import com.thebluealliance.androidclient.fragments.event.EventDistrictPointsFragment;
import com.thebluealliance.androidclient.fragments.event.EventInfoFragment;
import com.thebluealliance.androidclient.fragments.event.EventMatchesFragment;
import com.thebluealliance.androidclient.fragments.event.EventRankingsFragment;
import com.thebluealliance.androidclient.fragments.event.EventStatsFragment;
import com.thebluealliance.androidclient.fragments.event.EventTeamsFragment;
import com.thebluealliance.androidclient.fragments.gameday.GamedayWebcastsFragment;
import com.thebluealliance.androidclient.fragments.match.MatchInfoFragment;
import com.thebluealliance.androidclient.fragments.mytba.MyFavoritesFragment;
import com.thebluealliance.androidclient.fragments.mytba.MySubscriptionsFragment;
import com.thebluealliance.androidclient.fragments.team.TeamEventsFragment;
import com.thebluealliance.androidclient.fragments.team.TeamInfoFragment;
import com.thebluealliance.androidclient.fragments.team.TeamMediaFragment;
import com.thebluealliance.androidclient.fragments.teamAtEvent.TeamAtEventStatsFragment;
import com.thebluealliance.androidclient.fragments.teamAtEvent.TeamAtEventSummaryFragment;
import com.thebluealliance.androidclient.modules.BinderModule;
import com.thebluealliance.androidclient.modules.DatabaseWriterModule;
import com.thebluealliance.androidclient.modules.DatafeedModule;
import com.thebluealliance.androidclient.modules.SubscriberModule;
import com.thebluealliance.androidclient.subscribers.EventBusSubscriber;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(
  modules= {
    SubscriberModule.class,
    BinderModule.class,
    DatafeedModule.class,
    DatabaseWriterModule.class},
  dependencies = {ApplicationComponent.class})
public interface FragmentComponent {

    CacheableDatafeed datafeed();
    EventBusSubscriber eventBusSubscriber();

    void inject(TeamInfoFragment fragment);
    void inject(TeamEventsFragment fragment);
    void inject(TeamMediaFragment fragment);

    void inject(EventInfoFragment fragment);
    void inject(EventTeamsFragment fragment);
    void inject(EventRankingsFragment fragment);
    void inject(EventMatchesFragment fragment);
    void inject(EventAlliancesFragment fragment);
    void inject(EventDistrictPointsFragment fragment);
    void inject(EventStatsFragment fragment);
    void inject(EventAwardsFragment fragment);

    void inject(TeamAtEventStatsFragment fragment);
    void inject(TeamAtEventSummaryFragment fragment);

    void inject(EventsByWeekFragment fragment);
    void inject(EventListFragment fragment);
    void inject(TeamListFragment teamListFragment);
    void inject(DistrictListFragment districtListFragment);

    void inject(DistrictEventsFragment districtEventsFragment);
    void inject(DistrictRankingsFragment districtRankingsFragment);

    void inject(TeamAtDistrictSummaryFragment teamAtDistrictSummaryFragment);
    void inject(TeamAtDistrictBreakdownFragment teamAtDistrictBreakdownFragment);
    void inject(MatchInfoFragment matchInfoFragment);

    void inject(GamedayWebcastsFragment gamedayWebcastsFragment);

    void inject(RecentNotificationsFragment recentNotificationsFragment);

    void inject(MySubscriptionsFragment mySubscriptionsFragment);
    void inject(MyFavoritesFragment myFavoritesFragment);
}
