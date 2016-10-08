package com.thebluealliance.androidclient.di.components;

import com.thebluealliance.androidclient.datafeed.gce.GceModule;
import com.thebluealliance.androidclient.gcm.GCMMessageHandler;
import com.thebluealliance.androidclient.renderers.RendererModule;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(
  modules = {GceModule.class, RendererModule.class},
  dependencies = ApplicationComponent.class)
public interface NotificationComponent {
  void inject(GCMMessageHandler gcmMessageHandler);
}
