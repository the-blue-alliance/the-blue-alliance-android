package com.thebluealliance.androidclient.di.components;

import com.thebluealliance.androidclient.accounts.AccountModule;
import com.thebluealliance.androidclient.activities.MyTBASettingsActivity;
import com.thebluealliance.androidclient.activities.settings.SettingsActivity;
import com.thebluealliance.androidclient.auth.AuthModule;
import com.thebluealliance.androidclient.database.writers.DatabaseWriterModule;
import com.thebluealliance.androidclient.datafeed.DatafeedModule;
import com.thebluealliance.androidclient.datafeed.gce.GceModule;
import com.thebluealliance.androidclient.fragments.NavigationDrawerFragment;
import com.thebluealliance.androidclient.fragments.mytba.MyTBAFragment;
import com.thebluealliance.androidclient.fragments.mytba.MyTBASettingsFragment;
import com.thebluealliance.androidclient.mytba.MyTbaUpdateService;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(
        modules = {GceModule.class, DatafeedModule.class, DatabaseWriterModule.class,
                AccountModule.class, AuthModule.class},
        dependencies = {ApplicationComponent.class})
public interface MyTbaComponent {

    void inject(MyTbaUpdateService myTbaUpdateService);
    void inject(MyTBAFragment myTBAFragment);
    void inject(MyTBASettingsFragment myTBASettingsFragment);
    void inject(SettingsActivity.SettingsFragment settingsFragment);
    void inject(NavigationDrawerFragment navigationDrawerFragment);
    void inject(MyTBASettingsActivity myTBASettingsActivity);
}
