package com.thebluealliance.androidclient.datafeed.status;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.google.gson.Gson;
import com.thebluealliance.androidclient.BuildConfig;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.TbaLogger;
import com.thebluealliance.androidclient.Utilities;
import com.thebluealliance.androidclient.accounts.AccountController;
import com.thebluealliance.androidclient.activities.UpdateRequiredActivity;
import com.thebluealliance.androidclient.background.AnalyticsActions;
import com.thebluealliance.androidclient.models.ApiStatus;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import javax.inject.Inject;
import javax.inject.Singleton;

import okhttp3.Cache;
import rx.schedulers.Schedulers;

/**
 * A class to handle the TBA Status API
 * Can fire off {@link StatusRefreshService} to load new data
 */
@Singleton
public class TBAStatusController implements Application.ActivityLifecycleCallbacks {

    public static final String STATUS_PREF_KEY = "tba_status";
    private static final String STATUS_CACHE_CLEAR_KEY = "last_okcache_clear";

    /**
     * We use different timeouts for logged in users and not logged in users. Logged-in users will
     * receive push notifications when the status changes, so it's not as important to poll
     * frequently. For logged-out users, however, we want to poll more often because they won't
     * get push notifications.
     */
    // 15 minutes
    private static final double UPDATE_TIMEOUT_LOGGED_IN_NS = 9e+11;
    // 1 minute
    private static final double UPDATE_TIMEOUT_LOGGED_OUT_NS = 6e+10;

    // 3 hours
    private static final double DIALOG_TIMEOUT = 1.08e+13;

    private final SharedPreferences mPrefs;
    private final Gson mGson;
    private final Cache mOkHttpCache;

    private long mLastUpdateTime;
    private long mLastDialogTime;
    private long mLastMessageTime;

    private boolean mUserIsLoggedIn;

    @Inject
    public TBAStatusController(SharedPreferences prefs, Gson gson, Cache cache, AccountController accountController) {
        mPrefs = prefs;
        mGson = gson;
        mOkHttpCache = cache;
        mLastUpdateTime = Long.MIN_VALUE;
        mLastDialogTime = Long.MIN_VALUE;

        mUserIsLoggedIn = accountController.isMyTbaEnabled();
    }

    public void scheduleStatusUpdate(Context context) {
        StatusRefreshService.enqueueWork(context);
    }

    public @Nullable ApiStatus fetchApiStatus() {
        if (!mPrefs.contains(STATUS_PREF_KEY)) {
            return null;
        }

        String statusJson = mPrefs.getString(STATUS_PREF_KEY, "");
        return mGson.fromJson(statusJson, ApiStatus.class);
    }

    public int getMaxCompYear() {
        ApiStatus status = fetchApiStatus();
        if (status == null || status.getMaxSeason() == null) {
            Calendar cal = Calendar.getInstance();
            return cal.get(Calendar.YEAR);
        }
        return status.getMaxSeason();
    }

    public Integer getCurrentCompYear() {
        ApiStatus status = fetchApiStatus();
        if (status == null) {
            Calendar cal = Calendar.getInstance();
            return cal.get(Calendar.YEAR);
        }
        return status.getCurrentSeason();
    }

    public int getMinAppVersion() {
        ApiStatus status = fetchApiStatus();
        if (status == null || status.getMinAppVersion() == null) {
            /* Default to the current version */
            return BuildConfig.VERSION_CODE;
        }
        return status.getMinAppVersion();
    }

    private int getLatestAppVersion() {
        ApiStatus status = fetchApiStatus();
        if (status == null || status.getLatestAppVersion() == null) {
            /* Default to the current version */
            return BuildConfig.VERSION_CODE;
        }
        return status.getLatestAppVersion();
    }

    public void clearOkCacheIfNeeded(@Nullable ApiStatus status, boolean forceClear) {
        long lastCacheClear = mPrefs.getLong(STATUS_CACHE_CLEAR_KEY, Long.MIN_VALUE);
        if (status != null
                && mOkHttpCache != null
                && (forceClear || lastCacheClear < status.getLastOkHttpCacheClear())) {
            Schedulers.io().createWorker().schedule(() -> {
                TbaLogger.i("Clearing OkHttp cache");
                try {
                    mOkHttpCache.evictAll();
                    mPrefs.edit()
                            .putLong(STATUS_CACHE_CLEAR_KEY, System.currentTimeMillis() / 1000L)
                            .apply();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {
        new AnalyticsActions.ReportActivityStart(activity).run();
    }

    @Override
    public void onActivityResumed(Activity activity) {
        /* Update myTBA Status */
        double timeout = mUserIsLoggedIn ? UPDATE_TIMEOUT_LOGGED_IN_NS
                : UPDATE_TIMEOUT_LOGGED_OUT_NS;
        if (mLastUpdateTime + timeout < System.nanoTime()) {
            scheduleStatusUpdate(activity);
            mLastUpdateTime = System.nanoTime();
        }

        /* App updates required/recommended */
        if (!Utilities.isDebuggable() && BuildConfig.VERSION_CODE < getMinAppVersion()) {
            activity.startActivity(new Intent(activity, UpdateRequiredActivity.class));
        } else if (!Utilities.isDebuggable()
                && BuildConfig.VERSION_CODE < getLatestAppVersion()
                && mLastDialogTime + DIALOG_TIMEOUT < System.nanoTime()) {
            /* Show an app update dialog */
            new AlertDialog.Builder(activity)
                    .setTitle(R.string.update_dialog_title)
                    .setMessage(R.string.update_dialog_text)
                    .setPositiveButton(R.string.update_dialog_action, (dialog, which) -> {
                        /* Open Play Store page */
                        dialog.dismiss();
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.thebluealliance.androidclient"));
                        activity.startActivity(i);
                    })
                    .setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss())
                    .show();
            mLastDialogTime = System.nanoTime();
        }

        /* Admin push message */
        ApiStatus status = fetchApiStatus();
        Date now = new Date();
        if (status != null && status.getHasMessage()
                && mLastMessageTime + DIALOG_TIMEOUT < System.nanoTime()
                && status.getMessageExpiration().compareTo(now.getTime()) > 0) {
            new AlertDialog.Builder(activity)
                    .setMessage(status.getMessageText())
                    .setCancelable(false)
                    .setPositiveButton(R.string.ok, ((dialog, which) -> dialog.dismiss()))
                    .show();
            mLastMessageTime = System.nanoTime();
        }

        /* Clear OkHttp Cache when commanded */
        clearOkCacheIfNeeded(status, false);
    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {
        new AnalyticsActions.ReportActivityStop(activity).run();
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }
}
