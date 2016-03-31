package com.thebluealliance.androidclient.di;

import com.thebluealliance.androidclient.binders.DistrictPointsListBinder;
import com.thebluealliance.androidclient.binders.EventInfoBinder;
import com.thebluealliance.androidclient.binders.EventTabBinder;
import com.thebluealliance.androidclient.binders.ExpandableListViewBinder;
import com.thebluealliance.androidclient.binders.ListViewBinder;
import com.thebluealliance.androidclient.binders.MatchBreakdownBinder;
import com.thebluealliance.androidclient.binders.MatchListBinder;
import com.thebluealliance.androidclient.binders.NoDataBinder;
import com.thebluealliance.androidclient.binders.RecentNotificationsListBinder;
import com.thebluealliance.androidclient.binders.RecyclerViewBinder;
import com.thebluealliance.androidclient.binders.StatsListBinder;
import com.thebluealliance.androidclient.binders.TeamInfoBinder;
import com.thebluealliance.androidclient.fragments.framework.SimpleBinder;
import com.thebluealliance.androidclient.helpers.FragmentBinder;
import com.thebluealliance.androidclient.listeners.EventInfoContainerClickListener;
import com.thebluealliance.androidclient.listeners.SocialClickListener;
import com.thebluealliance.androidclient.renderers.MatchRenderer;
import com.thebluealliance.androidclient.renderers.ModelRendererSupplier;

import org.mockito.Mockito;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(includes = {MockRendererModule.class, MockClickListenerModule.class})
public class MockBinderModule {
    @Provides
    public FragmentBinder provideFragmentBinder() {
        return Mockito.mock(FragmentBinder.class);
    }

    @Provides
    public EventInfoBinder provideEventInfoBinder(MatchRenderer renderer, SocialClickListener socialClickListener, EventInfoContainerClickListener eventInfoContainerClickListener) {
        return Mockito.mock(EventInfoBinder.class);
    }

    @Provides
    public TeamInfoBinder provideTeamInfoBinder(SocialClickListener socialClickListener) {
        return Mockito.mock(TeamInfoBinder.class);
    }

    @Provides
    public ListViewBinder provideListviewBinder() {
        return Mockito.mock(ListViewBinder.class);
    }

    @Provides
    public RecyclerViewBinder provideRecyclerViewBinder() {
        return Mockito.mock(RecyclerViewBinder.class);
    }

    @Provides
    public RecentNotificationsListBinder provideRecentNotificationsListBinder() {
        return Mockito.mock(RecentNotificationsListBinder.class);
    }

    @Provides
    public ExpandableListViewBinder provideExpandableListBinder(ModelRendererSupplier supplier) {
        return Mockito.mock(ExpandableListViewBinder.class);
    }

    @Provides
    public StatsListBinder provideStatsListBinder() {
        return Mockito.mock(StatsListBinder.class);
    }

    @Provides
    public MatchListBinder provideMatchListBinder(ModelRendererSupplier supplier) {
        return Mockito.mock(MatchListBinder.class);
    }

    @Provides
    public EventTabBinder provideEventTabBinder() {
        return Mockito.mock(EventTabBinder.class);
    }

    @Provides
    public DistrictPointsListBinder provideDistrictPointsListBinder() {
        return Mockito.mock(DistrictPointsListBinder.class);
    }

    @Provides
    public MatchBreakdownBinder provideMatchbreakdownBinder() {
        return Mockito.mock(MatchBreakdownBinder.class);
    }

    @Provides
    public NoDataBinder provideNoDataBinder() {
        return Mockito.mock(NoDataBinder.class);
    }

    @Provides @Singleton
    public SimpleBinder provideSimpleBinder() {
        return Mockito.mock(SimpleBinder.class);
    }
}
