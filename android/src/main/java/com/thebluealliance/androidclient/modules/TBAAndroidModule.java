package com.thebluealliance.androidclient.modules;

import com.thebluealliance.androidclient.TBAAndroid;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.database.DatabaseWriter;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * App-wide dependency injection items
 */
@Module
public class TBAAndroidModule {
    static TBAAndroid mApp;

    public TBAAndroidModule() {}

    public TBAAndroidModule(TBAAndroid app) {
        mApp = app;
    }

    /* UNCOMMENT WHEN NEEDED
    @Provides @Singleton
    public Context provideApplicationContext() {
        return mApp.getApplicationContext();
    }
    */

    @Provides @Singleton
    public Database provideDatabase() {
        return Database.getInstance(mApp);
    }

    @Provides @Singleton
    public DatabaseWriter provideDatabaseWriter(Database db) {
        return new DatabaseWriter(db);
    }
}
