package com.thebluealliance.androidclient.di.components;

import com.thebluealliance.androidclient.di.TBAAndroidModule;
import com.thebluealliance.androidclient.subscribers.SubscriberModule;

import dagger.Component;

@Component(modules = {TBAAndroidModule.class})
public interface ApplicationComponent {
    void inject(SubscriberModule module);

    void inject(TBAAndroidModule module);
}
