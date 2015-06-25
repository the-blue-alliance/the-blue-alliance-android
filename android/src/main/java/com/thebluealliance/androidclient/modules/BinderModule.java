package com.thebluealliance.androidclient.modules;

import com.thebluealliance.androidclient.binders.EventInfoBinder;

import dagger.Module;
import dagger.Provides;

@Module
public class BinderModule {

    @Provides
    public EventInfoBinder provideEventInfoBinder() {
        return new EventInfoBinder();
    }
}
