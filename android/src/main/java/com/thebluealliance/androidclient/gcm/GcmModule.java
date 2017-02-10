package com.thebluealliance.androidclient.gcm;

import com.google.firebase.iid.FirebaseInstanceId;

import com.thebluealliance.androidclient.LocalProperties;
import com.thebluealliance.androidclient.di.TBAAndroidModule;

import android.content.SharedPreferences;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(includes = TBAAndroidModule.class)
public class GcmModule {

    @Provides @Singleton
    public FirebaseInstanceId provideFirebaseInstanceId() {
        return FirebaseInstanceId.getInstance();
    }

    @Provides @Singleton
    public GcmController provideGcmController(LocalProperties localProperties,
                                              SharedPreferences sharedPreferences) {
        return new GcmController(localProperties, sharedPreferences);
    }
}
