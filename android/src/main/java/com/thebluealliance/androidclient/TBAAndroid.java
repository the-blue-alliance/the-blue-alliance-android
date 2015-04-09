package com.thebluealliance.androidclient;

import android.app.Application;
import android.util.Log;

import com.facebook.stetho.Stetho;

/**
 * File created by phil on 7/21/14.
 */
public class TBAAndroid extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(Constants.LOG_TAG, "Welcome to The Blue Alliance for Android, v" + BuildConfig.VERSION_NAME);
        if(false && Utilities.isDebuggable()){
            Stetho.initialize(
                    Stetho.newInitializerBuilder(this)
                            .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                            .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
                            .build());
        }
    }
}
