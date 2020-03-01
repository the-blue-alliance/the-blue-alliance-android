package com.thebluealliance.androidclient.di;

import com.google.firebase.perf.FirebasePerformance;
import com.thebluealliance.androidclient.tracing.TracingController;

import javax.inject.Singleton;

import androidx.annotation.Nullable;
import dagger.Module;
import dagger.Provides;

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
