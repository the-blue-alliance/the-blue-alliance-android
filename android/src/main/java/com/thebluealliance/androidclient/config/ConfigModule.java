package com.thebluealliance.androidclient.config;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import com.thebluealliance.androidclient.BuildConfig;
import com.thebluealliance.androidclient.TbaLogger;

import javax.annotation.Nullable;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ConfigModule {

    @Provides @Singleton @Nullable
    public FirebaseRemoteConfig provideFirebaseRemoteConfig() {
        try {
            FirebaseRemoteConfig config = FirebaseRemoteConfig.getInstance();
            FirebaseRemoteConfigSettings settings = new FirebaseRemoteConfigSettings.Builder()
                    .setDeveloperModeEnabled(BuildConfig.DEBUG)
                    .build();
            config.setConfigSettings(settings);
            return config;
        } catch (IllegalStateException ex) {
            /* When there is no google-secrets.json file found, the library throws an exception
             * here which causes insta-crashes for us. Silently recover here...
             */
            TbaLogger.e("Unable to find google-secrets.json, disabling remote config");
            return null;
        }
    }

    @Provides @Singleton
    public AppConfig provideAppConfig(@Nullable FirebaseRemoteConfig config) {
        return new AppConfig(config);
    }
}
