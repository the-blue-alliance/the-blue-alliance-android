package com.thebluealliance.androidclient.di.components;

import com.thebluealliance.androidclient.TbaAndroid;
import com.thebluealliance.androidclient.accounts.AccountModule;
import com.thebluealliance.androidclient.activities.HomeActivity;
import com.thebluealliance.androidclient.activities.LaunchActivity;
import com.thebluealliance.androidclient.activities.settings.DevSettingsActivity;
import com.thebluealliance.androidclient.background.LoadTBADataTaskFragment;
import com.thebluealliance.androidclient.config.ConfigModule;
import com.thebluealliance.androidclient.datafeed.CacheableDatafeed;
import com.thebluealliance.androidclient.datafeed.DatafeedModule;
import com.thebluealliance.androidclient.datafeed.status.StatusRefreshService;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(
  modules = {DatafeedModule.class, AccountModule.class, ConfigModule.class},
  dependencies = {ApplicationComponent.class})
public interface DatafeedComponent {

    CacheableDatafeed datafeed();

    void inject(StatusRefreshService statusRefreshService);

    void inject(TbaAndroid tbaAndroid);
    void inject(HomeActivity homeActivity);
    void inject(LaunchActivity launchActivity);
    void inject(LoadTBADataTaskFragment loadTBADataTaskFragment);
    void inject(DevSettingsActivity.DevSettingsFragment fragment);
}
