package com.thebluealliance.androidclient;

import android.app.Application;
import android.util.Log;

/**
 * File created by phil on 7/21/14.
 */
public class TBAAndroid extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(Constants.LOG_TAG, "Welcome to The Blue Alliance for Android, v" + BuildConfig.VERSION_NAME);
    }
}
