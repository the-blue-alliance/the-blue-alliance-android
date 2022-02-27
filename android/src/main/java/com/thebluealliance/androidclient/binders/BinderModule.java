package com.thebluealliance.androidclient.binders;

import android.content.Context;
import android.content.res.Resources;

import com.thebluealliance.androidclient.helpers.FragmentBinder;
import com.thebluealliance.androidclient.listeners.EventInfoContainerClickListener;
import com.thebluealliance.androidclient.listeners.SocialClickListener;
import com.thebluealliance.androidclient.renderers.MatchRenderer;
import com.thebluealliance.androidclient.renderers.ModelRendererSupplier;

import org.greenrobot.eventbus.EventBus;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ActivityComponent;
import dagger.hilt.android.qualifiers.ApplicationContext;

@InstallIn(ActivityComponent.class)
@Module
public class BinderModule {

    @Provides
    public FragmentBinder provideFragmentBinder() {
        return new FragmentBinder();
    }

    @Provides
    public EventInfoBinder provideEventInfoBinder(MatchRenderer renderer, SocialClickListener socialClickListener, EventInfoContainerClickListener eventInfoContainerClickListener) {
        return new EventInfoBinder(renderer, socialClickListener, eventInfoContainerClickListener);
    }

    @Provides
    public TeamInfoBinder provideTeamInfoBinder(SocialClickListener socialClickListener) {
        return new TeamInfoBinder(socialClickListener);
    }

    @Provides
    public ListViewBinder provideListviewBinder() {
        return new ListViewBinder();
    }

    @Provides
    RecyclerViewBinder provideRecyclerViewBinder() {
        return new RecyclerViewBinder();
    }

    @Provides
    public RecentNotificationsListBinder provideRecentNotificationsListBinder() {
        return new RecentNotificationsListBinder();
    }

    @Provides
    public ExpandableListViewBinder provideExpandableListBinder(ModelRendererSupplier supplier) {
        return new ExpandableListViewBinder(supplier);
    }

    @Provides
    public StatsListBinder provideStatsListBinder() {
        return new StatsListBinder();
    }

    @Provides
    public MatchListBinder provideMatchListBinder(ModelRendererSupplier supplier) {
        return new MatchListBinder(supplier);
    }

    @Provides
    public EventTabBinder provideEventTabBinder() {
        return new EventTabBinder();
    }

    @Provides
    public DistrictPointsListBinder provideDistrictPointsListBinder(@ApplicationContext Context context) {
        return new DistrictPointsListBinder(context.getResources());
    }

    @Provides
    public NoDataBinder provideNoDataBinder() {
        return new NoDataBinder();
    }

    @Provides
    public MatchBreakdownBinder provideMatchBreakdownBinder() {
        return new MatchBreakdownBinder();
    }

    @Provides
    public DistrictEventsBinder provideDistrictEventsBinder(EventBus eventBus, Resources res) {
        return new DistrictEventsBinder(eventBus, res);
    }
}
