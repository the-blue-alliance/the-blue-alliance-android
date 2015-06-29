package com.thebluealliance.androidclient.modules;

import com.thebluealliance.androidclient.TBAAndroid;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.database.DatabaseWriter;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * App-wide dependency injection items
 */
@Module
public class TBAAndroidModule {
    static TBAAndroid mApp;
    @Inject Database mDb;

    public TBAAndroidModule() {
        mApp.getComponent().inject(this);
    }

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
    public DatabaseWriter provideDatabaseWriter() {
        return new DatabaseWriter(mDb);
    }
}
