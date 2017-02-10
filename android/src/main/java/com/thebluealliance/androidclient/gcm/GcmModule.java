package com.thebluealliance.androidclient.gcm;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import com.thebluealliance.androidclient.config.AppConfig;
import com.thebluealliance.androidclient.config.ConfigModule;
import com.thebluealliance.androidclient.di.TBAAndroidModule;

import android.content.Context;
import android.content.SharedPreferences;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(includes = {TBAAndroidModule.class, ConfigModule.class})
public class GcmModule {

    @Provides @Singleton
    public GoogleCloudMessaging provideGoogleCloudMessaging(Context context) {
        return GoogleCloudMessaging.getInstance(context);
    }

    @Provides @Singleton
    public GcmController provideGcmController(AppConfig appConfig,
                                              SharedPreferences sharedPreferences) {
        return new GcmController(appConfig, sharedPreferences);
    }
}
