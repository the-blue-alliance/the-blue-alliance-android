package com.thebluealliance.androidclient.di.components;

import android.support.v4.app.Fragment;

import com.thebluealliance.androidclient.database.DatabaseWriter;
import com.thebluealliance.androidclient.datafeed.APICache;
import com.thebluealliance.androidclient.datafeed.CacheableDatafeed;
import com.thebluealliance.androidclient.datafeed.refresh.RefreshController;
import com.thebluealliance.androidclient.datafeed.maps.RetrofitResponseMap;
import com.thebluealliance.androidclient.datafeed.DatafeedModule;
import com.thebluealliance.androidclient.di.TBAAndroidModule;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(
  modules = {DatafeedModule.class, TBAAndroidModule.class},
  dependencies = {ApplicationComponent.class})
public interface DatafeedComponent {
    void inject(CacheableDatafeed datafeed);
    void inject(DatafeedModule module);
    void inject(Fragment fragment);
    void inject(APICache cache);

    APICache cache();
    CacheableDatafeed datafeed();
    RefreshController refreshController();
    DatabaseWriter databaseWriter();
    RetrofitResponseMap retrofitResponseMap();
}
