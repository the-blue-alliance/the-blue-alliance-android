package com.thebluealliance.androidclient.modules.components;

import com.thebluealliance.androidclient.TBAAndroid;
import com.thebluealliance.androidclient.modules.TBAAndroidModule;
import dagger.Component;

import javax.inject.Singleton;

@Singleton
@Component(modules = {TBAAndroidModule.class})
public interface ApplicationComponent {
    void inject(TBAAndroid app);
}
