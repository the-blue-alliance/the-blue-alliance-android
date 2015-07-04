package com.thebluealliance.androidclient.modules.components;

import com.thebluealliance.androidclient.datafeed.CacheableDatafeed;
import com.thebluealliance.androidclient.fragments.event.EventAlliancesFragment;
import com.thebluealliance.androidclient.fragments.event.EventAwardsFragment;
import com.thebluealliance.androidclient.fragments.event.EventDistrictPointsFragment;
import com.thebluealliance.androidclient.fragments.event.EventInfoFragment;
import com.thebluealliance.androidclient.fragments.event.EventMatchesFragment;
import com.thebluealliance.androidclient.fragments.event.EventRankingsFragment;
import com.thebluealliance.androidclient.fragments.event.EventStatsFragment;
import com.thebluealliance.androidclient.fragments.event.EventTeamsFragment;
import com.thebluealliance.androidclient.fragments.team.TeamEventsFragment;
import com.thebluealliance.androidclient.fragments.team.TeamInfoFragment;
import com.thebluealliance.androidclient.fragments.team.TeamMediaFragment;
import com.thebluealliance.androidclient.fragments.teamAtEvent.TeamAtEventStatsFragment;
import com.thebluealliance.androidclient.fragments.teamAtEvent.TeamAtEventSummaryFragment;
import com.thebluealliance.androidclient.modules.BinderModule;
import com.thebluealliance.androidclient.modules.DatabaseWriterModule;
import com.thebluealliance.androidclient.modules.DatafeedModule;
import com.thebluealliance.androidclient.modules.SubscriberModule;
import com.thebluealliance.androidclient.subscribers.MultiEndpointSubscriber;

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
    MultiEndpointSubscriber multiSubscriber();

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
}
