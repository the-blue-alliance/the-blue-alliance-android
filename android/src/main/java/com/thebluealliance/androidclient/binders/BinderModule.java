package com.thebluealliance.androidclient.binders;

import com.thebluealliance.androidclient.helpers.FragmentBinder;
import com.thebluealliance.androidclient.listeners.ClickListenerModule;
import com.thebluealliance.androidclient.listeners.EventInfoContainerClickListener;
import com.thebluealliance.androidclient.listeners.SocialClickListener;
import com.thebluealliance.androidclient.renderers.MatchRenderer;
import com.thebluealliance.androidclient.renderers.ModelRendererSupplier;
import com.thebluealliance.androidclient.renderers.RendererModule;

import android.content.res.Resources;

import dagger.Module;
import dagger.Provides;

@Module(includes = {RendererModule.class, ClickListenerModule.class})
public class BinderModule {

    private final Resources mResources;

    public BinderModule(Resources resources) {
        mResources = resources;
    }

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
    public DistrictPointsListBinder provideDistrictPointsListBinder() {
        return new DistrictPointsListBinder(mResources);
    }

    @Provides
    public NoDataBinder provideNoDataBinder() {
        return new NoDataBinder();
    }

    @Provides
    public MatchBreakdownBinder provideMatchBreakdownBinder() {
        return new MatchBreakdownBinder();
    }
}
