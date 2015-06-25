package com.thebluealliance.androidclient;

import android.support.multidex.MultiDexApplication;
import android.util.Log;
import com.facebook.stetho.Stetho;

public class TBAAndroid extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(Constants.LOG_TAG, "Welcome to The Blue Alliance for Android, v" + BuildConfig.VERSION_NAME);
        if (Utilities.isDebuggable()) {
            Stetho.initialize(
                    Stetho.newInitializerBuilder(this)
                            .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                            .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
                            .build());
        }
    }
}
