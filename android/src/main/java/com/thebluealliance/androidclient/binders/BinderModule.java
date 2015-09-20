package com.thebluealliance.androidclient.binders;

import com.thebluealliance.androidclient.helpers.FragmentBinder;

import dagger.Module;
import dagger.Provides;

@Module
public class BinderModule {

    @Provides
    public FragmentBinder provideFragmentBinder() {
        return new FragmentBinder();
    }

    @Provides
    public EventInfoBinder provideEventInfoBinder() {
        return new EventInfoBinder();
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
    public NoDataBinder provideNoDataBinder() {
        return new NoDataBinder();
    }
}
