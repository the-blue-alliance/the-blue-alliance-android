package com.thebluealliance.androidclient.di;

import com.thebluealliance.androidclient.di.components.DatafeedComponent;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(
        modules = {MockDatafeedModule.class, MockConfigModule.class},
        dependencies = {MockApplicationComponent.class})
public interface MockDatafeedComponent extends DatafeedComponent{
}
