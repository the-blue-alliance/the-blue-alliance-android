package com.thebluealliance.androidclient.di;

import com.thebluealliance.androidclient.gcm.GCMMessageHandlerWithMocks;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(
        modules = {MockGceModule.class, MockRendererModule.class, MockConfigModule.class, MockGcmModule.class, MockAccountModule.class, MockAuthModule.class},
        dependencies = MockApplicationComponent.class)
public interface MockNotificationComponent {
    void inject(GCMMessageHandlerWithMocks handlerWithMocks);
}
