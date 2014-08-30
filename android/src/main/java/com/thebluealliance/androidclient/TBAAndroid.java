package com.thebluealliance.androidclient;

import android.app.Application;
import android.util.Log;

import com.google.android.gms.analytics.Tracker;
import com.thebluealliance.androidclient.accounts.AccountHelper;
import com.thebluealliance.androidclient.background.UpdateMyTBA;

/**
 * File created by phil on 7/21/14.
 */
public class TBAAndroid extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(Constants.LOG_TAG, "Welcome to The Blue Alliance for Android, v" + BuildConfig.VERSION_NAME);

        if(AccountHelper.isAccountSelected(this)){
            new UpdateMyTBA(this, true).execute();
        }
    }

    public Tracker getTracker(Analytics.GAnalyticsTracker tracker) {
        return Analytics.getTracker(tracker, this);
    }


}
