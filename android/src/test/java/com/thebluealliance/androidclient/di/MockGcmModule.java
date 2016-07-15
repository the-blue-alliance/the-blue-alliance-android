package com.thebluealliance.androidclient.di;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.thebluealliance.androidclient.gcm.GcmController;

import org.mockito.Mockito;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(includes = MockTbaAndroidModule.class)
public class MockGcmModule {
    @Provides
    @Singleton
    public GoogleCloudMessaging provideGoogleCloudMessaging() {
        return Mockito.mock(GoogleCloudMessaging.class);
    }

    @Provides @Singleton
    public GcmController provideGcmController() {
        return Mockito.mock(GcmController.class);
    }
}
