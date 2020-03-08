package com.thebluealliance.androidclient.di;

import com.google.gson.Gson;
import com.thebluealliance.androidclient.datafeed.HttpModule;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(includes = {MockTbaAndroidModule.class})
public class MockHttpModule {

    @Provides
    @Singleton
    public Gson provideGson() {
        return HttpModule.getGson();
    }
}
