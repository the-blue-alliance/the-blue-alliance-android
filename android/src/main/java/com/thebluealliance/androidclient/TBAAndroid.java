package com.thebluealliance.androidclient;

import android.app.Application;
import android.util.Log;

import com.facebook.stetho.Stetho;
import com.thebluealliance.androidclient.modules.DatafeedModule;
import com.thebluealliance.androidclient.modules.TBAAndroidModule;

public class TBAAndroid extends Application {

    private TBAAndroidModule mModule;
    private DatafeedModule mDatafeedModule;

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

    public TBAAndroidModule getModule() {
        if (mModule == null) {
            mModule = new TBAAndroidModule(this);
        }
        return mModule;
    }
}
