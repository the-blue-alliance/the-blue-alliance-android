package com.thebluealliance.androidclient.di;


import com.thebluealliance.androidclient.BaseTestActivity;
import com.thebluealliance.androidclient.di.components.FragmentComponent;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(
        modules= {
                MockSubscriberModule.class,
                MockBinderModule.class,
                MockDatafeedModule.class,
                MockDatabaseWriterModule.class,
                MockAccountModule.class,
                MockClickListenerModule.class,
                MockGceModule.class},
        dependencies = {MockApplicationComponent.class})
public interface MockFragmentComponent extends FragmentComponent {

    void inject(BaseTestActivity baseTestActivity);
}
