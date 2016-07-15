package com.thebluealliance.androidclient.accounts;

import com.thebluealliance.androidclient.LocalProperties;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.di.TBAAndroidModule;

import android.accounts.AccountManager;
import android.content.SharedPreferences;
import android.content.res.Resources;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(includes = TBAAndroidModule.class)
public class AccountModule {

    @Provides @Singleton
    public AccountController provideAccountController(SharedPreferences preferences,
                                                      AccountManager accountManager,
                                                      LocalProperties localProperties,
                                                      Resources resources) {
        String accountType = resources.getString(R.string.account_type);
        return new AccountController(preferences, accountManager, localProperties, accountType);
    }
}
