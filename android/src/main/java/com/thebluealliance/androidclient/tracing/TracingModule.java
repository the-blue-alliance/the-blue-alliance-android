package com.thebluealliance.androidclient.tracing;

import com.google.firebase.perf.FirebasePerformance;
import com.thebluealliance.androidclient.TbaLogger;

import javax.inject.Singleton;

import androidx.annotation.Nullable;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@InstallIn(SingletonComponent.class)
@Module
public class TracingModule {

    public TracingModule() {

    }

    @Provides @Singleton @Nullable
    public FirebasePerformance provideFirebasePerformance() {
        try {
            return FirebasePerformance.getInstance();
        } catch (IllegalStateException ex) {
            /* When there is no google-secrets.json file found, the library throws an exception
             * here which causes insta-crashes for us. Silently recover here...
             */
            TbaLogger.e("Unable to find google-secrets.json, disabling firebase tracing");
            return null;
        }
    }

    @Provides @Singleton
    public TracingController provideTracingController(@Nullable FirebasePerformance firebasePerf) {
        return new TracingController(firebasePerf);
    }
}
