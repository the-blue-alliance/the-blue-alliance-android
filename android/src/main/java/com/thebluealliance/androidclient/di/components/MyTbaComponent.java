package com.thebluealliance.androidclient.di.components;

import com.thebluealliance.androidclient.database.writers.DatabaseWriterModule;
import com.thebluealliance.androidclient.datafeed.DatafeedModule;
import com.thebluealliance.androidclient.datafeed.gce.GceModule;
import com.thebluealliance.androidclient.mytba.MyTbaUpdateService;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(
        modules = {GceModule.class, DatafeedModule.class, DatabaseWriterModule.class},
        dependencies = {ApplicationComponent.class})
public interface MyTbaComponent {

    void inject(MyTbaUpdateService myTbaUpdateService);
}
