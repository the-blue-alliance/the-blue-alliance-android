package com.thebluealliance.androidclient.di.components;

import com.thebluealliance.androidclient.database.writers.DatabaseWriterModule;
import com.thebluealliance.androidclient.datafeed.DatafeedModule;
import com.thebluealliance.androidclient.gcm.GCMMessageHandler;
import com.thebluealliance.androidclient.renderers.RendererModule;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(
  modules = {DatafeedModule.class, DatabaseWriterModule.class, RendererModule.class},
  dependencies = ApplicationComponent.class)
public interface NotificationComponent {
  void inject(GCMMessageHandler gcmMessageHandler);
}
