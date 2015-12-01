package com.thebluealliance.androidclient.binders;

import android.content.res.Resources;

import com.thebluealliance.androidclient.helpers.FragmentBinder;
import com.thebluealliance.androidclient.renderers.MatchRenderer;
import com.thebluealliance.androidclient.renderers.RendererModule;

import dagger.Module;
import dagger.Provides;

@Module(includes = RendererModule.class)
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
    public EventInfoBinder provideEventInfoBinder(MatchRenderer renderer) {
        return new EventInfoBinder(renderer);
    }

    @Provides
    public TeamInfoBinder provideTeamInfoBinder() {
        return new TeamInfoBinder();
    }

    @Provides
    public ListViewBinder provideListviewBinder() {
        return new ListViewBinder();
    }

    @Provides
    public ExpandableListViewBinder provideExpandableListBinder() {
        return new ExpandableListViewBinder();
    }

    @Provides
    public StatsListBinder provideStatsListBinder() {
        return new StatsListBinder();
    }

    @Provides
    public MatchListBinder provideMatchListBinder() {
        return new MatchListBinder();
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
}
