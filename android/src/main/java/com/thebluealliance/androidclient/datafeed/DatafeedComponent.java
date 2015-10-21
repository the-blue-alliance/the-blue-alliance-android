package com.thebluealliance.androidclient.datafeed;

import com.thebluealliance.androidclient.di.components.ApplicationComponent;
import com.thebluealliance.androidclient.gcm.GCMMessageHandler;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(
  modules = {DatafeedModule.class},
  dependencies = {ApplicationComponent.class})
public interface DatafeedComponent {
    void inject(GCMMessageHandler gcmMessageHandler);
}
