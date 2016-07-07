package com.thebluealliance.androidclient.di;


import com.thebluealliance.androidclient.BaseTestActivity;
import com.thebluealliance.androidclient.di.components.FragmentComponent;
import com.thebluealliance.androidclient.fragments.framework.SimpleDatafeedFragment;

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
                MockClickListenerModule.class},
        dependencies = {MockApplicationComponent.class})
public interface MockFragmentComponent extends FragmentComponent {

    void inject(SimpleDatafeedFragment fragment);

    void inject(BaseTestActivity baseTestActivity);
}
