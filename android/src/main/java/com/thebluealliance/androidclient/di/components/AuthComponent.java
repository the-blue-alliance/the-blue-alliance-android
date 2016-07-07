package com.thebluealliance.androidclient.di.components;

import com.thebluealliance.androidclient.activities.MyTBAOnboardingActivity;
import com.thebluealliance.androidclient.activities.OnboardingActivity;
import com.thebluealliance.androidclient.auth.AuthModule;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AuthModule.class})
public interface AuthComponent {

    void inject(MyTBAOnboardingActivity activity);
    void inject(OnboardingActivity onboardingActivity);
}
