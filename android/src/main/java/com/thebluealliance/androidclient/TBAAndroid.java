package com.thebluealliance.androidclient;

import android.app.Application;
import android.util.Log;

import com.facebook.stetho.Stetho;

import java.util.Arrays;
import java.util.List;

import dagger.ObjectGraph;

public class TBAAndroid extends Application {

    private ObjectGraph mObjectGraph;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(Constants.LOG_TAG, "Welcome to The Blue Alliance for Android, v" + BuildConfig.VERSION_NAME);
        mObjectGraph = ObjectGraph.create(getModules().toArray());
        mObjectGraph.inject(this);
        if (Utilities.isDebuggable()) {
            Stetho.initialize(
                    Stetho.newInitializerBuilder(this)
                            .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                            .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
                            .build());
        }
    }

    private List<Object> getModules() {
        return Arrays.asList(new TBAAndroidModule(this));
    }

    public ObjectGraph createScopedGraph(Object... modules) {
        return mObjectGraph.plus(modules);
    }
}
