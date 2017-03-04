package com.thebluealliance.androidclient.di;

import com.thebluealliance.androidclient.config.AppConfig;

import org.mockito.Mockito;

import dagger.Module;
import dagger.Provides;

@Module
public class MockConfigModule {

    @Provides
    public AppConfig provideAppConfig() {
        return Mockito.mock(AppConfig.class);
    }
}
