package com.thebluealliance.androidclient.di;

import com.thebluealliance.androidclient.BaseTestActivity;
import com.thebluealliance.androidclient.di.components.MyTbaComponent;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(
        modules = {MockDatabaseWriterModule.class, MockAccountModule.class, MockAuthModule.class,
                   MockGcmModule.class, MockGceModule.class, MockDatafeedModule.class},
        dependencies = {MockApplicationComponent.class})
public interface MockMyTbaComponent extends MyTbaComponent {

    void inject(BaseTestActivity baseTestActivity);
}
