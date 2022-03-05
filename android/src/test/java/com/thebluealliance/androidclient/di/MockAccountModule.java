package com.thebluealliance.androidclient.di;

import com.thebluealliance.androidclient.accounts.AccountController;
import com.thebluealliance.androidclient.accounts.AccountModule;

import org.mockito.Mockito;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.components.SingletonComponent;
import dagger.hilt.testing.TestInstallIn;

@TestInstallIn(components = SingletonComponent.class, replaces = AccountModule.class)
@Module()
public class MockAccountModule {

    public MockAccountModule() {}

    @Provides @Singleton
    public AccountController provideAccountController() {
        return Mockito.mock(AccountController.class);
    }
}
