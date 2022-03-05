package com.thebluealliance.androidclient.di;

import com.thebluealliance.androidclient.datafeed.HttpModule;

import dagger.Module;
import dagger.hilt.components.SingletonComponent;
import dagger.hilt.testing.TestInstallIn;

@Module
@TestInstallIn(components = SingletonComponent.class, replaces = HttpModule.class)
public class MockHttpModule {
/*
    @Provides
    @Singleton
    public OkHttpClient getOkHttp() {
        return Mockito.mock(OkHttpClient.class);
    }


    @Provides @Singleton
    public Cache provideOkCache(@ApplicationContext Context context) {
        return new Cache(context.getCacheDir(), HttpModule.CACHE_SIZE);
    }
 */
}
