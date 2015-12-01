package com.thebluealliance.androidclient.di.components;

import com.thebluealliance.androidclient.TBAAndroid;
import com.thebluealliance.androidclient.datafeed.CacheableDatafeed;
import com.thebluealliance.androidclient.datafeed.DatafeedModule;
import com.thebluealliance.androidclient.datafeed.status.StatusRefreshService;
import com.thebluealliance.androidclient.gcm.GCMMessageHandler;
import com.thebluealliance.androidclient.mytba.MyTbaUpdateService;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(
  modules = {DatafeedModule.class},
  dependencies = {ApplicationComponent.class})
public interface DatafeedComponent {

    CacheableDatafeed datafeed();

    void inject(GCMMessageHandler gcmMessageHandler);
    void inject(StatusRefreshService statusRefreshService);
    void inject(MyTbaUpdateService myTbaUpdateService);

    void inject(TBAAndroid tbaAndroid);
}
