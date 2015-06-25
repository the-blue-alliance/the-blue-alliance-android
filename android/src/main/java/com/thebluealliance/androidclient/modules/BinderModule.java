package com.thebluealliance.androidclient.modules;

import com.thebluealliance.androidclient.binders.EventInfoBinder;
import com.thebluealliance.androidclient.binders.EventListBinder;
import com.thebluealliance.androidclient.binders.MediaListBinder;
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
    public EventListBinder provideEventListBinder() {
        return new EventListBinder();
    }

    @Provides
    public MediaListBinder provideMediaListBinder() {
        return new MediaListBinder();
    }
}
