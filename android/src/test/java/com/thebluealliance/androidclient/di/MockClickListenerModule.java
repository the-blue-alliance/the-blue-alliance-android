package com.thebluealliance.androidclient.di;

import com.thebluealliance.androidclient.listeners.ContributorClickListener;
import com.thebluealliance.androidclient.listeners.EventInfoContainerClickListener;
import com.thebluealliance.androidclient.listeners.SocialClickListener;

import org.mockito.Mockito;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class MockClickListenerModule {

    @Provides @Singleton
    public SocialClickListener provideSocialClickListener() {
        return Mockito.mock(SocialClickListener.class);
    }

    @Provides @Singleton
    public EventInfoContainerClickListener provideEventInfoContainerClickListener() {
        return Mockito.mock(EventInfoContainerClickListener.class);
    }

    @Provides @Singleton
    public ContributorClickListener provideContributorClickListener() {
        return Mockito.mock(ContributorClickListener.class);
    }
}
