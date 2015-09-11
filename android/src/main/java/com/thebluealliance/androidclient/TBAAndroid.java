package com.thebluealliance.androidclient;

import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.facebook.stetho.Stetho;
import com.thebluealliance.androidclient.binders.BinderModule;
import com.thebluealliance.androidclient.database.writers.DatabaseWriterModule;
import com.thebluealliance.androidclient.datafeed.DatafeedModule;
import com.thebluealliance.androidclient.di.TBAAndroidModule;
import com.thebluealliance.androidclient.di.components.ApplicationComponent;
import com.thebluealliance.androidclient.di.components.DaggerApplicationComponent;

public class TBAAndroid extends MultiDexApplication {

    private ApplicationComponent mComponent;
    private TBAAndroidModule mModule;
    private DatafeedModule mDatafeedModule;
    private BinderModule mBinderModule;
    private DatabaseWriterModule mDatabaseWriterModule;

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

    public DatafeedModule getDatafeedModule() {
        if (mDatafeedModule == null) {
            mDatafeedModule = new DatafeedModule();
        }
        return mDatafeedModule;
    }

    public BinderModule getBinderModule() {
        if (mBinderModule == null) {
            mBinderModule = new BinderModule();
        }
        return mBinderModule;
    }

    public DatabaseWriterModule getDatabaseWriterModule() {
        if (mDatabaseWriterModule == null) {
            mDatabaseWriterModule = new DatabaseWriterModule();
        }
        return mDatabaseWriterModule;
    }

    public ApplicationComponent getComponent() {
        if (mComponent == null) {
            mComponent = DaggerApplicationComponent.builder()
              .tBAAndroidModule(getModule())
              .build();
        }
        return mComponent;
    }
}
