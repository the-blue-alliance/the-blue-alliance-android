package com.thebluealliance.androidclient.di;

import dagger.Component;

@Component(modules = {MockTbaAndroidModule.class})
public interface MockApplicationComponent extends ApplicationComponent {
}
