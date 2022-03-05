package com.thebluealliance.androidclient.di;

import com.thebluealliance.androidclient.listeners.ClickListenerModule;
import com.thebluealliance.androidclient.listeners.ContributorClickListener;
import com.thebluealliance.androidclient.listeners.EventInfoContainerClickListener;
import com.thebluealliance.androidclient.listeners.SocialClickListener;

import org.mockito.Mockito;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.android.components.ActivityComponent;
import dagger.hilt.testing.TestInstallIn;

@TestInstallIn(components = ActivityComponent.class, replaces = ClickListenerModule.class)
@Module
public class MockClickListenerModule {

    @Provides
    public SocialClickListener provideSocialClickListener() {
        return Mockito.mock(SocialClickListener.class);
    }

    @Provides
    public EventInfoContainerClickListener provideEventInfoContainerClickListener() {
        return Mockito.mock(EventInfoContainerClickListener.class);
    }

    @Provides
    public ContributorClickListener provideContributorClickListener() {
        return Mockito.mock(ContributorClickListener.class);
    }
}
