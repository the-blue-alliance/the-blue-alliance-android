package com.thebluealliance.androidclient.di;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

import android.accounts.AccountManager;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.android.gms.analytics.Tracker;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.database.DatabaseWithMocks;

import org.greenrobot.eventbus.EventBus;
import org.mockito.Mockito;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;
import dagger.hilt.testing.TestInstallIn;

@Module
@TestInstallIn(components = SingletonComponent.class, replaces = TBAAndroidModule.class)
public class MockTbaAndroidModule {

    @Provides @Singleton
    public Gson provideGson() {
        return TBAAndroidModule.getGson();
    }

    @Provides @Singleton
    public Picasso providePicasso() {
        return mock(Picasso.class);
    }

    @Provides @Singleton
    public Database provideDatabase(@ApplicationContext Context context, Gson gson) {
        Database db = spy(new DatabaseWithMocks(context, gson));
        return db;
    }

    @Provides
    @Singleton
    public SharedPreferences provideSharedPrefs(@ApplicationContext Context context) {
        return context.getSharedPreferences("prefs", 0);
    }

    @Provides
    @Singleton
    public EventBus provideEventBus() {
        return Mockito.mock(EventBus.class);
    }

    @Provides
    @Singleton
    public Tracker provideAndroidTracker() {
        return Mockito.mock(Tracker.class);
    }

    @Provides @Singleton
    public AccountManager provideAccountManager() {
        return Mockito.mock(AccountManager.class);
    }

    @Provides @Singleton
    public FirebaseRemoteConfig provideFirebaseRemoteConfig() {
        return Mockito.mock(FirebaseRemoteConfig.class);
    }
}
