package com.thebluealliance.androidclient;

import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.datafeed.APICache;
import com.thebluealliance.androidclient.datafeed.APIv2RequestInterceptor;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * App-wide dependency injection items
 */
@Module(
  injects = {
    TBAAndroid.class,
    APICache.class,
    APIv2RequestInterceptor.class
  }
)
public class TBAAndroidModule {
    static TBAAndroid mApp;

    public TBAAndroidModule() {

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
}
