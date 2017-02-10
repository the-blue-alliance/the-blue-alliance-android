package com.thebluealliance.androidclient.gcm;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import com.thebluealliance.androidclient.config.LocalProperties;
import com.thebluealliance.androidclient.di.TBAAndroidModule;

import android.content.Context;
import android.content.SharedPreferences;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(includes = TBAAndroidModule.class)
public class GcmModule {

    @Provides @Singleton
    public GoogleCloudMessaging provideGoogleCloudMessaging(Context context) {
        return GoogleCloudMessaging.getInstance(context);
    }

    @Provides @Singleton
    public GcmController provideGcmController(LocalProperties localProperties,
                                              SharedPreferences sharedPreferences) {
        return new GcmController(localProperties, sharedPreferences);
    }
}
