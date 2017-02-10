package com.thebluealliance.androidclient.config;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import com.thebluealliance.androidclient.Analytics;
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

    public String getString(String key) {
        if (mFirebaseRemoteConfig == null) {
            return "";
        }
        return mFirebaseRemoteConfig.getString(key);
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

                        /* Update the analytics ID in a static class
                         * This is horrible, nasty, good-for-nothing, hacky, and disgusting
                         * Here, we atone for sins of the past
                         */
                        Analytics.setAnalyticsId(mFirebaseRemoteConfig.getString(Analytics.PROD_ANALYTICS_KEY));
                    } else {
                        TbaLogger.e("Unable to update remote config", task.getException());
                    }
                });
    }
}
