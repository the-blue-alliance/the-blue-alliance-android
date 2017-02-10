package com.thebluealliance.androidclient.di.components;

import com.thebluealliance.androidclient.accounts.AccountModule;
import com.thebluealliance.androidclient.activities.MyTBASettingsActivity;
import com.thebluealliance.androidclient.activities.settings.MyTBAModelSettingsActivity;
import com.thebluealliance.androidclient.activities.settings.SettingsActivity;
import com.thebluealliance.androidclient.auth.AuthModule;
import com.thebluealliance.androidclient.auth.firebase.MigrateLegacyUserToFirebase;
import com.thebluealliance.androidclient.config.ConfigModule;
import com.thebluealliance.androidclient.database.writers.DatabaseWriterModule;
import com.thebluealliance.androidclient.datafeed.gce.GceModule;
import com.thebluealliance.androidclient.fragments.NavigationDrawerFragment;
import com.thebluealliance.androidclient.fragments.mytba.MyTBAFragment;
import com.thebluealliance.androidclient.fragments.mytba.MyTBASettingsFragment;
import com.thebluealliance.androidclient.fragments.tasks.UpdateUserModelSettingsTaskFragment;
import com.thebluealliance.androidclient.gcm.GcmModule;
import com.thebluealliance.androidclient.mytba.MyTbaRegistrationService;
import com.thebluealliance.androidclient.mytba.MyTbaUpdateService;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(
        modules = {GceModule.class, DatabaseWriterModule.class,
                AccountModule.class, AuthModule.class, GcmModule.class, ConfigModule.class},
        dependencies = {ApplicationComponent.class})
public interface MyTbaComponent {

    void inject(MyTbaUpdateService myTbaUpdateService);
    void inject(MyTbaRegistrationService myTbaRegistrationService);
    void inject(MyTBAFragment myTBAFragment);
    void inject(MyTBASettingsFragment myTBASettingsFragment);
    void inject(SettingsActivity.SettingsFragment settingsFragment);
    void inject(NavigationDrawerFragment navigationDrawerFragment);
    void inject(MyTBASettingsActivity myTBASettingsActivity);
    void inject(UpdateUserModelSettingsTaskFragment updateUserModelSettingsTaskFragment);
    void inject(MyTBAModelSettingsActivity myTBAModelSettingsActivity);
    void inject(MigrateLegacyUserToFirebase migrateLegacyUserToFirebase);
}
