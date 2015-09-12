package com.thebluealliance.androidclient.di.components;

import com.thebluealliance.androidclient.TBAAndroid;
import com.thebluealliance.androidclient.subscribers.SubscriberModule;
import com.thebluealliance.androidclient.di.TBAAndroidModule;

import dagger.Component;

@Component(modules = {TBAAndroidModule.class})
public interface ApplicationComponent {
    void inject(TBAAndroid app);
    void inject(SubscriberModule module);
    void inject(TBAAndroidModule module);
}
