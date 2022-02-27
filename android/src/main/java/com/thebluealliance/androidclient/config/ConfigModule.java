package com.thebluealliance.androidclient.config;

import android.content.SharedPreferences;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.thebluealliance.androidclient.TbaLogger;

import javax.annotation.Nullable;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import dagger.hilt.migration.DisableInstallInCheck;

@InstallIn(SingletonComponent.class)
@Module
public class ConfigModule {

    @Provides @Singleton @Nullable
    public FirebaseRemoteConfig provideFirebaseRemoteConfig() {
        try {
            return FirebaseRemoteConfig.getInstance();
        } catch (IllegalStateException ex) {
            /* When there is no google-secrets.json file found, the library throws an exception
             * here which causes insta-crashes for us. Silently recover here...
             */
            TbaLogger.e("Unable to find google-secrets.json, disabling remote config");
            return null;
        }
    }

    @Provides @Singleton
    public AppConfig provideAppConfig(@Nullable FirebaseRemoteConfig config, SharedPreferences prefs) {
        return new AppConfig(config, prefs);
    }
}
