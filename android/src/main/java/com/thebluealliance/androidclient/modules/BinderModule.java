package com.thebluealliance.androidclient.modules;

import com.thebluealliance.androidclient.binders.EventInfoBinder;
import com.thebluealliance.androidclient.binders.ExpandableListBinder;
import com.thebluealliance.androidclient.binders.ListviewBinder;
import com.thebluealliance.androidclient.binders.TeamInfoBinder;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class BinderModule {

    @Provides @Singleton
    public EventInfoBinder provideEventInfoBinder() {
        return new EventInfoBinder();
    }

    @Provides @Singleton
    public TeamInfoBinder provideTeamInfoBinder() {
        return new TeamInfoBinder();
    }

    @Provides @Singleton
    public ListviewBinder provideListviewBinder() {
        return new ListviewBinder();
    }

    @Provides @Singleton
    public ExpandableListBinder provideMediaListBinder() {
        return new ExpandableListBinder();
    }
}
