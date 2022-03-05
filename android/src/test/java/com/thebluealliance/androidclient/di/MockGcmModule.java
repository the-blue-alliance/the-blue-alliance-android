package com.thebluealliance.androidclient.di;

import com.google.firebase.messaging.FirebaseMessaging;
import com.thebluealliance.androidclient.gcm.GcmController;
import com.thebluealliance.androidclient.gcm.GcmModule;

import org.mockito.Mockito;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.components.SingletonComponent;
import dagger.hilt.testing.TestInstallIn;

@TestInstallIn(components = SingletonComponent.class, replaces = GcmModule.class)
@Module
public class MockGcmModule {
    @Provides
    @Singleton
    public FirebaseMessaging provideFirebaseMessaging() {
        return Mockito.mock(FirebaseMessaging.class);
    }

    @Provides @Singleton
    public GcmController provideGcmController() {
        return Mockito.mock(GcmController.class);
    }
}
