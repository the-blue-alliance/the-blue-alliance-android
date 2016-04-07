package com.thebluealliance.androidclient.di;

import com.thebluealliance.androidclient.di.components.ApplicationComponent;

import dagger.Component;

@Component(modules = {MockTbaAndroidModule.class})
public interface MockApplicationComponent extends ApplicationComponent {
}
