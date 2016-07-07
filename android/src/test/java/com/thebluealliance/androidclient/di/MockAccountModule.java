package com.thebluealliance.androidclient.di;

import com.thebluealliance.androidclient.accounts.AccountController;

import org.mockito.Mockito;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(includes = MockTbaAndroidModule.class)
public class MockAccountModule {

    public MockAccountModule() {}

    @Provides @Singleton
    public AccountController provideAccountController() {
        return Mockito.mock(AccountController.class);
    }
}
