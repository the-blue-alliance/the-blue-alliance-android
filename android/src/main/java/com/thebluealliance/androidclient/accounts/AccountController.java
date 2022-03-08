package com.thebluealliance.androidclient.accounts;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.Nullable;

import com.thebluealliance.androidclient.TbaLogger;
import com.thebluealliance.androidclient.auth.User;
import com.thebluealliance.androidclient.config.AppConfig;
import com.thebluealliance.androidclient.mytba.MyTbaRegistrationWorker;
import com.thebluealliance.androidclient.mytba.MyTbaUpdateWorker;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * A class to handle state surrounding MyTba
 * Backed by {@link SharedPreferences}
 */
@Singleton
public class AccountController {

    private static final String PREF_MYTBA_ENABLED = "mytba_enabled";
    private static final String PREF_SELECTED_ACCOUNT = "selected_account";

    private final SharedPreferences mPreferences;
    private final AccountManager mAccountManager;
    private final AppConfig mAppConfig;
    private final String mAccountType;

    @Inject
    public AccountController(
            SharedPreferences preferences,
            AccountManager accountManager,
            AppConfig appConfig,
            String accountType) {
        mPreferences = preferences;
        mAccountManager = accountManager;
        mAppConfig = appConfig;
        mAccountType = accountType;
    }

    public void setMyTbaEnabled(boolean enabled) {
        mPreferences.edit().putBoolean(PREF_MYTBA_ENABLED, enabled).apply();
    }

    public boolean isMyTbaEnabled() {
        return mPreferences.getBoolean(PREF_MYTBA_ENABLED, false) && !getWebClientId().isEmpty();
    }

    public void setSelectedAccount(String account) {
        mPreferences.edit().putString(PREF_SELECTED_ACCOUNT, account).apply();
    }

    public String getSelectedAccount() {
        return mPreferences.getString(PREF_SELECTED_ACCOUNT, "");
    }

    public boolean isAccountSelected() {
        return !getSelectedAccount().isEmpty();
    }

    public @Nullable Account getCurrentAccount() {
        Account[] accounts;
        try {
            accounts = mAccountManager.getAccountsByType(mAccountType);
        } catch (SecurityException e) {
            // We don't have the correct permissions
            TbaLogger.w("Can't get current local account, no permission", e);
            return null;
        }
        String selectedAccount = getSelectedAccount();
        for (Account account : accounts) {
            if (account.name.equals(selectedAccount)) return account;
        }
        return null;
    }

    public void onAccountConnect(Context context, User user) {
        setMyTbaEnabled(true);
        registerSystemAccount(user.getEmail());
        setSelectedAccount(user.getEmail());
        registerForGcm(context);
        loadMyTbaData(context);
    }

    public String getWebClientId() {
        return mAppConfig.getString("appspot_webClientId");
    }

    private boolean registerSystemAccount(String accountName) {
        Account[] accounts;
        try {
            accounts = mAccountManager.getAccountsByType(mAccountType);
        } catch (SecurityException e) {
            // We don't have the correct permissions
            TbaLogger.w("Can't add local account, no permission", e);
            return false;
        }
        if (accounts.length == 0) {
            Account account = new Account(accountName, mAccountType);
            return mAccountManager.addAccountExplicitly(account, null, null);
        }

        // An account of this type already exists
        return true;
    }

    private void registerForGcm(Context context) {
        MyTbaRegistrationWorker.run(context);
    }

    private void loadMyTbaData(Context context) {
        MyTbaUpdateWorker.run(context, true, true);
    }

}
