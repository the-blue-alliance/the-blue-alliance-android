package com.thebluealliance.androidclient.di.components;

import com.thebluealliance.androidclient.datafeed.CacheableDatafeed;
import com.thebluealliance.androidclient.datafeed.DatafeedModule;
import com.thebluealliance.androidclient.gcm.GCMMessageHandler;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(
  modules = {DatafeedModule.class},
  dependencies = {ApplicationComponent.class})
public interface DatafeedComponent {

    CacheableDatafeed datafeed();

    void inject(GCMMessageHandler gcmMessageHandler);
}
