package com.thebluealliance.androidclient.listeners;

import android.content.Context;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ActivityComponent;
import dagger.hilt.android.components.ActivityRetainedComponent;
import dagger.hilt.android.qualifiers.ActivityContext;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;
import dagger.hilt.migration.DisableInstallInCheck;

@InstallIn(ActivityComponent.class)
@Module
public class ClickListenerModule {

    @Provides
    public SocialClickListener provideSocialClickListener(@ActivityContext Context context) {
        return new SocialClickListener(context);
    }

    @Provides
    public EventInfoContainerClickListener provideEventInfoContainerClickListener(@ActivityContext Context context) {
        return new EventInfoContainerClickListener(context);
    }

    @Provides
    public ContributorClickListener provideContributorClickListener(@ActivityContext Context context) {
        return new ContributorClickListener(context);
    }
}
