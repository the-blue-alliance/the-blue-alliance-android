package com.thebluealliance.androidclient.modules;

import com.thebluealliance.androidclient.binders.EventInfoBinder;
import com.thebluealliance.androidclient.binders.EventTabBinder;
import com.thebluealliance.androidclient.binders.ExpandableListViewBinder;
import com.thebluealliance.androidclient.binders.ListViewBinder;
import com.thebluealliance.androidclient.binders.MatchListBinder;
import com.thebluealliance.androidclient.binders.NoDataBinder;
import com.thebluealliance.androidclient.binders.StatsListBinder;
import com.thebluealliance.androidclient.binders.TeamInfoBinder;

import dagger.Module;
import dagger.Provides;

@Module
public class BinderModule {

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
