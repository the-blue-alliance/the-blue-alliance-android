package com.thebluealliance.androidclient.modules.components;

import com.thebluealliance.androidclient.TBAAndroid;
import com.thebluealliance.androidclient.modules.SubscriberModule;
import com.thebluealliance.androidclient.modules.TBAAndroidModule;

import dagger.Component;

@Component(modules = {TBAAndroidModule.class})
public interface ApplicationComponent {
    void inject(TBAAndroid app);
    void inject(SubscriberModule module);
    void inject(TBAAndroidModule module);
}
