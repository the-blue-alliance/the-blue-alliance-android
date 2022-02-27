package com.thebluealliance.androidclient.gcm;

import android.content.SharedPreferences;

import com.google.firebase.messaging.FirebaseMessaging;
import com.thebluealliance.androidclient.TbaLogger;
import com.thebluealliance.androidclient.config.AppConfig;
import com.thebluealliance.androidclient.config.ConfigModule;
import com.thebluealliance.androidclient.di.TBAAndroidModule;

import javax.annotation.Nullable;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import dagger.hilt.migration.DisableInstallInCheck;

@InstallIn(SingletonComponent.class)
@Module(includes = {TBAAndroidModule.class, ConfigModule.class})
public class GcmModule {

    @Provides @Singleton @Nullable
    public FirebaseMessaging provideFirebaseMessaging() {
        try {
            return FirebaseMessaging.getInstance();
        } catch (IllegalStateException ex) {
            /* When there is no google-secrets.json file found, the library throws an exception
             * here which causes insta-crashes for us. Silently recover here...
             */
            TbaLogger.e("Unable to find google-secrets.json, disabling firebase messaging");
            return null;
        }
    }

    @Provides @Singleton
    public GcmController provideGcmController(AppConfig appConfig,
                                              SharedPreferences sharedPreferences) {
        return new GcmController(appConfig, sharedPreferences);
    }
}
