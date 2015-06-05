package com.thebluealliance.androidclient;

import dagger.Module;

@Module(
        injects = {
                TBAAndroid.class
        }
)
public class TBAAndroidModule {
    private TBAAndroid mApp;

    public TBAAndroidModule(TBAAndroid app){
        mApp = app;
    }

    /* UNCOMMENT WHEN NEEDED
    @Provides
    @Singleton
    public Context provideApplicationContext() {
        return mApp.getApplicationContext();
    }
    */
}
