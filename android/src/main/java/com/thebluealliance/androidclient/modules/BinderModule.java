package com.thebluealliance.androidclient.modules;

import com.thebluealliance.androidclient.binders.EventInfoBinder;
import com.thebluealliance.androidclient.binders.ExpandableListBinder;
import com.thebluealliance.androidclient.binders.ListviewBinder;
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
    public ListviewBinder provideListviewBinder() {
        return new ListviewBinder();
    }

    @Provides
    public ExpandableListBinder provideMediaListBinder() {
        return new ExpandableListBinder();
    }
}
