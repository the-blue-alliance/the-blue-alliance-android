package com.thebluealliance.androidclient.modules.components;

import android.support.v4.app.Fragment;

import com.thebluealliance.androidclient.database.DatabaseWriter;
import com.thebluealliance.androidclient.datafeed.APICache;
import com.thebluealliance.androidclient.datafeed.CacheableDatafeed;
import com.thebluealliance.androidclient.datafeed.ObservableCache;
import com.thebluealliance.androidclient.datafeed.RetrofitConverter;
import com.thebluealliance.androidclient.modules.DatafeedModule;
import com.thebluealliance.androidclient.modules.TBAAndroidModule;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(
  modules = {DatafeedModule.class, TBAAndroidModule.class},
  dependencies = {ApplicationComponent.class})
public interface DatafeedComponent {
    void inject(CacheableDatafeed datafeed);
    void inject(DatafeedModule module);
    void inject(RetrofitConverter converter);
    void inject(Fragment fragment);
    void inject(APICache cache);

    APICache cache();
    CacheableDatafeed datafeed();
    DatabaseWriter databaseWriter();
    ObservableCache observableCache();
}
