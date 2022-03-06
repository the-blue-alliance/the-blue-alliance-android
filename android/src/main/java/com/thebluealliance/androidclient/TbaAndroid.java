package com.thebluealliance.androidclient;

import static com.thebluealliance.androidclient.gcm.notifications.BaseNotification.NOTIFICATION_CHANNEL;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.hilt.work.HiltWorkerFactory;
import androidx.multidex.MultiDexApplication;
import androidx.work.Configuration;

import com.facebook.stetho.Stetho;
import com.thebluealliance.androidclient.config.AppConfig;
import com.thebluealliance.androidclient.datafeed.status.TBAStatusController;

import javax.inject.Inject;

import dagger.hilt.android.HiltAndroidApp;

@HiltAndroidApp
public class TbaAndroid extends MultiDexApplication implements Configuration.Provider {

    @Inject TBAStatusController mStatusController;
    @Inject AppConfig mAppConfig;
    @Inject HiltWorkerFactory mWorkerFactory;

    private boolean mShouldBindStetho;

    public TbaAndroid() {
        super();
        mShouldBindStetho = true;
    }

    /**
     * Firebase Crash Reporting makes it so the application is multi-process. Each process will
     * run the Application's onCreate during initialization.
     * Therefore, ensure there are no accesses to shared resources here, otherwise we could
     * get some strange race conditions
     */
    @Override
    public void onCreate() {
        super.onCreate();
        TbaLogger.i("Welcome to The Blue Alliance for Android, v" + BuildConfig.VERSION_NAME);
        mAppConfig.updateRemoteData();
        registerActivityLifecycleCallbacks(mStatusController);

        if (Utilities.isDebuggable() && mShouldBindStetho) {
            Stetho.initialize(
                    Stetho.newInitializerBuilder(this)
                            .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                            .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))

                            .build());
        }

        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.notification_channel_name);
            String description = getString(R.string.notification_channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        // Set up global dark mode flags
        String darkModePref = PreferenceManager.getDefaultSharedPreferences(this).getString("dark_mode", "");
        TbaLogger.d("Setting theme to " + darkModePref + " at startup");
        AppCompatDelegate.setDefaultNightMode(Utilities.getCurrentDarkModePreference(darkModePref));

    }

    void disableStetho() {
        mShouldBindStetho = false;
    }

    @NonNull
    @Override
    public Configuration getWorkManagerConfiguration() {
        return new Configuration.Builder()
                .setWorkerFactory(mWorkerFactory)
                .build();
    }
}
