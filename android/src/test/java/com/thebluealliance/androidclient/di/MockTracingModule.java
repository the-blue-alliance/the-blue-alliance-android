package com.thebluealliance.androidclient.di;

import com.google.firebase.perf.FirebasePerformance;
import com.thebluealliance.androidclient.tracing.TracingController;
import com.thebluealliance.androidclient.tracing.TracingModule;

import javax.inject.Singleton;

import androidx.annotation.Nullable;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.components.SingletonComponent;
import dagger.hilt.testing.TestInstallIn;

@TestInstallIn(components = SingletonComponent.class, replaces = TracingModule.class)
@Module
public class MockTracingModule {

    @Provides
    @Singleton
    @Nullable
    public FirebasePerformance provideFirebasePerformance() {
        return null;
    }

    @Provides @Singleton
    public TracingController provideTracingController() {
        return new TracingController(null);
    }
}
