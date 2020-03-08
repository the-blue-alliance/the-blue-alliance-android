package com.thebluealliance.androidclient.di;

import com.thebluealliance.androidclient.database.DatabaseWithMocks;
import com.thebluealliance.androidclient.di.components.DbComponent;

import javax.inject.Singleton;

import dagger.Component;


@Singleton
@Component(modules = {MockTbaAndroidModule.class, MockHttpModule.class})
public interface MockDbComponent extends DbComponent {
    void inject(DatabaseWithMocks db);
}
