package com.thebluealliance.androidclient.accounts;

import android.accounts.AccountManager;
import android.content.SharedPreferences;
import android.content.res.Resources;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.config.AppConfig;
import com.thebluealliance.androidclient.di.TBAAndroidModule;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import dagger.hilt.migration.DisableInstallInCheck;

@InstallIn(SingletonComponent.class)
@Module(includes = TBAAndroidModule.class)
public class AccountModule {

    @Provides @Singleton
    public AccountController provideAccountController(SharedPreferences preferences,
                                                      AccountManager accountManager,
                                                      AppConfig appConfig,
                                                      Resources resources) {
        String accountType = resources.getString(R.string.account_type);
        return new AccountController(preferences, accountManager, appConfig, accountType);
    }
}
