package com.thebluealliance.androidclient.di;

import com.thebluealliance.androidclient.config.AppConfig;
import com.thebluealliance.androidclient.config.ConfigModule;

import org.mockito.Mockito;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.components.SingletonComponent;
import dagger.hilt.testing.TestInstallIn;

@TestInstallIn(components = SingletonComponent.class, replaces = ConfigModule.class)
@Module
public class MockConfigModule {

    @Provides
    public AppConfig provideAppConfig() {
        return Mockito.mock(AppConfig.class);
    }
}
