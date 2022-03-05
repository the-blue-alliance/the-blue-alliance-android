package com.thebluealliance.androidclient.di;

import android.content.Context;

import com.thebluealliance.androidclient.datafeed.HttpModule;

import org.mockito.Mockito;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;
import dagger.hilt.testing.TestInstallIn;
import okhttp3.Cache;
import okhttp3.OkHttpClient;

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
