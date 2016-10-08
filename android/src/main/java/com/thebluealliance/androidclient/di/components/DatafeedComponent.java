package com.thebluealliance.androidclient.di.components;

import com.thebluealliance.androidclient.TBAAndroid;
import com.thebluealliance.androidclient.accounts.AccountModule;
import com.thebluealliance.androidclient.activities.HomeActivity;
import com.thebluealliance.androidclient.activities.LaunchActivity;
import com.thebluealliance.androidclient.background.LoadTBADataTaskFragment;
import com.thebluealliance.androidclient.datafeed.CacheableDatafeed;
import com.thebluealliance.androidclient.datafeed.DatafeedModule;
import com.thebluealliance.androidclient.datafeed.status.StatusRefreshService;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(
  modules = {DatafeedModule.class, AccountModule.class},
  dependencies = {ApplicationComponent.class})
public interface DatafeedComponent {

    CacheableDatafeed datafeed();

    void inject(StatusRefreshService statusRefreshService);

    void inject(TBAAndroid tbaAndroid);
    void inject(HomeActivity homeActivity);
    void inject(LaunchActivity launchActivity);
    void inject(LoadTBADataTaskFragment loadTBADataTaskFragment);
}
