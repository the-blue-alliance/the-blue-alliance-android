package com.thebluealliance.androidclient.config;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import com.thebluealliance.androidclient.TbaLogger;

import javax.annotation.Nullable;
import javax.inject.Inject;

public class AppConfig {

    private static final long CACHE_EXIPIRATION = 3600; // One hour in seconds

    private final @Nullable FirebaseRemoteConfig mFirebaseRemoteConfig;

    @Inject
    public AppConfig(@Nullable FirebaseRemoteConfig firebaseRemoteConfig) {
        mFirebaseRemoteConfig = firebaseRemoteConfig;
    }


    public void updateRemoteData() {
        if (mFirebaseRemoteConfig == null) {
            return;
        }

        boolean isDeveloperMode = mFirebaseRemoteConfig.getInfo()
                                                       .getConfigSettings()
                                                       .isDeveloperModeEnabled();
        TbaLogger.i("Updating remote configuration");
        mFirebaseRemoteConfig.fetch(isDeveloperMode ? 0 : CACHE_EXIPIRATION)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        TbaLogger.i("Remote config update succeeded");
                        mFirebaseRemoteConfig.activateFetched();
                    } else {
                        TbaLogger.e("Unable to update remote config", task.getException());
                    }
                });
    }
}
