package com.thebluealliance.androidclient.config;

import android.content.SharedPreferences;
import androidx.annotation.WorkerThread;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigFetchException;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigFetchThrottledException;
import com.thebluealliance.androidclient.Analytics;
import com.thebluealliance.androidclient.TbaLogger;
import com.thebluealliance.androidclient.datafeed.APIv3RequestInterceptor;

import java.util.concurrent.ExecutionException;

import javax.annotation.Nullable;
import javax.inject.Inject;

public class AppConfig {

    private static final long CACHE_EXIPIRATION = 3600; // One hour in seconds

    private final @Nullable FirebaseRemoteConfig mFirebaseRemoteConfig;
    private final SharedPreferences mPrefs;
    private Task<Void> mActiveTask;

    @Inject
    public AppConfig(@Nullable FirebaseRemoteConfig firebaseRemoteConfig, SharedPreferences prefs) {
        mFirebaseRemoteConfig = firebaseRemoteConfig;
        mPrefs = prefs;
        mActiveTask = null;
    }

    public String getString(String key) {
        if (mFirebaseRemoteConfig == null) {
            return "";
        }
        return mFirebaseRemoteConfig.getString(key);
    }

    public boolean getBoolean(String key) {
        return mFirebaseRemoteConfig != null && mFirebaseRemoteConfig.getBoolean(key);
    }

    public long getLong(String key, long defaultValue) {
        if (mFirebaseRemoteConfig == null) return defaultValue;
        return mFirebaseRemoteConfig.getLong(key);
    }

    public void updateRemoteData() {
        updateDataInternal(null);
    }

    public void updateRemoteData(OnCompleteListener<Void> onCompleteListener) {
        updateDataInternal(onCompleteListener);
    }

    @WorkerThread
    public void updateRemoteDataBlocking() throws ExecutionException, InterruptedException {
        updateDataInternal(null);
        if (mActiveTask != null) {
            Tasks.await(mActiveTask);
        }

        // Wait for the onCompleteHandler to finish
        while (mActiveTask != null) {
            Thread.sleep(100);
        }
    }

    private void updateDataInternal(@Nullable OnCompleteListener<Void> onComplete) {
        if (mFirebaseRemoteConfig == null) {
            return;
        }

        if (mActiveTask != null) {
            TbaLogger.w("Already updating remote config...");
            return;
        }

        boolean isDeveloperMode = mFirebaseRemoteConfig.getInfo()
                                                       .getConfigSettings()
                                                       .isDeveloperModeEnabled();

        TbaLogger.i("Updating remote configuration");
        mActiveTask = mFirebaseRemoteConfig.fetch(isDeveloperMode ? 0 : CACHE_EXIPIRATION)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        TbaLogger.i("Remote config update succeeded");
                        mFirebaseRemoteConfig.activateFetched();

                        /* Update the analytics ID in a static class
                         * This is horrible, nasty, good-for-nothing, hacky, and disgusting
                         * Here, we atone for sins of the past
                         */
                        Analytics.setAnalyticsId(mFirebaseRemoteConfig.getString(Analytics.PROD_ANALYTICS_KEY));
                        APIv3RequestInterceptor.updateApiKeys(mFirebaseRemoteConfig, mPrefs);
                    } else {
                        handleUpdateException(task.getException());
                    }
                    mActiveTask = null;
                })
                .addOnFailureListener(this::handleUpdateException);
        if (onComplete != null) {
            mActiveTask.addOnCompleteListener(onComplete);
        }
    }

    private void handleUpdateException(Exception e) {
        if (e instanceof FirebaseRemoteConfigFetchThrottledException) {
            TbaLogger.d("RemoteConfig fetch throttled");
        } else if (e instanceof FirebaseRemoteConfigFetchException) {
            TbaLogger.i("Error fetching RemoteConfig: " + e.getMessage());
        } else {
            TbaLogger.w("Error fetching RemoteConfig: " + e.getMessage(), e);
        }
    }
}
