package com.thebluealliance.androidclient.di;

import android.content.Context;

import com.thebluealliance.androidclient.binders.DistrictEventsBinder;
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

import org.greenrobot.eventbus.EventBus;
import org.mockito.Mockito;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(includes = {MockRendererModule.class, MockClickListenerModule.class})
public class MockBinderModule {

    private Context mContext;

    public MockBinderModule(Context context) {
        mContext = context;
    }

    @Provides
    public FragmentBinder provideFragmentBinder() {
        return Mockito.mock(FragmentBinder.class);
    }

    @Provides
    public EventInfoBinder provideEventInfoBinder(MatchRenderer renderer, SocialClickListener socialClickListener, EventInfoContainerClickListener eventInfoContainerClickListener) {
        return Mockito.spy(new EventInfoBinder(renderer, socialClickListener, eventInfoContainerClickListener));
    }

    @Provides
    public TeamInfoBinder provideTeamInfoBinder(SocialClickListener socialClickListener) {
        return Mockito.spy(new TeamInfoBinder(socialClickListener));
    }

    @Provides
    public ListViewBinder provideListviewBinder() {
        return Mockito.spy(new ListViewBinder());
    }

    @Provides
    public RecyclerViewBinder provideRecyclerViewBinder() {
        return Mockito.spy(new RecyclerViewBinder());
    }

    @Provides
    public RecentNotificationsListBinder provideRecentNotificationsListBinder() {
        return Mockito.spy(new RecentNotificationsListBinder());
    }

    @Provides
    public ExpandableListViewBinder provideExpandableListBinder(ModelRendererSupplier supplier) {
        return Mockito.spy(new ExpandableListViewBinder(supplier));
    }

    @Provides
    public StatsListBinder provideStatsListBinder() {
        return Mockito.spy(new StatsListBinder());
    }

    @Provides
    public MatchListBinder provideMatchListBinder(ModelRendererSupplier supplier) {
        return Mockito.spy(new MatchListBinder(supplier));
    }

    @Provides
    public EventTabBinder provideEventTabBinder() {
        return Mockito.mock(EventTabBinder.class);
    }

    @Provides
    public DistrictPointsListBinder provideDistrictPointsListBinder() {
        return Mockito.spy(new DistrictPointsListBinder(mContext.getResources()));
    }

    @Provides
    public DistrictEventsBinder provideDistrictEventsBinder() {
        EventBus eventBus = Mockito.mock(EventBus.class);
        return Mockito.spy(new DistrictEventsBinder(eventBus, mContext.getResources()));
    }

    @Provides
    public MatchBreakdownBinder provideMatchbreakdownBinder() {
        return Mockito.spy(new MatchBreakdownBinder());
    }

    @Provides
    public NoDataBinder provideNoDataBinder() {
        return Mockito.spy(new NoDataBinder());
    }

    @Provides @Singleton
    public SimpleBinder provideSimpleBinder() {
        return Mockito.mock(SimpleBinder.class);
    }
}
