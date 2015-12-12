package com.thebluealliance.androidclient;

import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.facebook.stetho.Stetho;
import com.thebluealliance.androidclient.binders.BinderModule;
import com.thebluealliance.androidclient.database.writers.DatabaseWriterModule;
import com.thebluealliance.androidclient.datafeed.DatafeedModule;
import com.thebluealliance.androidclient.datafeed.status.TBAStatusController;
import com.thebluealliance.androidclient.di.TBAAndroidModule;
import com.thebluealliance.androidclient.di.components.ApplicationComponent;
import com.thebluealliance.androidclient.di.components.DaggerApplicationComponent;
import com.thebluealliance.androidclient.di.components.DaggerDatafeedComponent;
import com.thebluealliance.androidclient.di.components.DatafeedComponent;

import javax.inject.Inject;

public class TBAAndroid extends MultiDexApplication {

    @Inject
    TBAStatusController mStatusController;

    private ApplicationComponent mComponent;
    private TBAAndroidModule mModule;
    private DatafeedModule mDatafeedModule;
    private BinderModule mBinderModule;
    private DatabaseWriterModule mDatabaseWriterModule;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(Constants.LOG_TAG, "Welcome to The Blue Alliance for Android, v" + BuildConfig.VERSION_NAME);
        getDatafeedComponenet().inject(this);
        registerActivityLifecycleCallbacks(mStatusController);

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
            mBinderModule = new BinderModule(getResources());
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

    private DatafeedComponent getDatafeedComponenet() {
        return DaggerDatafeedComponent.builder()
                .applicationComponent(getComponent())
                .datafeedModule(getDatafeedModule())
                .build();
    }
}
