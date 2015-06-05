package com.thebluealliance.androidclient;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

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

    @Provides
    @Singleton
    public Context provideApplicationContext() {
        return mApp.getApplicationContext();
    }
}
