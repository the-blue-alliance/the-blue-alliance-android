package com.thebluealliance.androidclient.accounts;

import com.thebluealliance.androidclient.LocalProperties;
import com.thebluealliance.androidclient.auth.User;
import com.thebluealliance.androidclient.mytba.MyTbaRegistrationService;
import com.thebluealliance.androidclient.mytba.MyTbaUpdateService;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;

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
    private final LocalProperties mLocalProperties;
    private final String mAccountType;

    @Inject
    public AccountController(
            SharedPreferences preferences,
            AccountManager accountManager,
            LocalProperties localProperties,
            String accountType) {
        mPreferences = preferences;
        mAccountManager = accountManager;
        mLocalProperties = localProperties;
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
        Account[] accounts = mAccountManager.getAccountsByType(mAccountType);
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
        return mLocalProperties.readLocalProperty("appspot.webClientId");
    }

    private boolean registerSystemAccount(String accountName) {
        if (mAccountManager.getAccountsByType(mAccountType).length == 0) {
            Account account = new Account(accountName, mAccountType);
            return mAccountManager.addAccountExplicitly(account, null, null);
        }

        // An account of this type already exists
        return true;
    }

    private void registerForGcm(Context context) {
        context.startService(new Intent(context, MyTbaRegistrationService.class));
    }

    private void loadMyTbaData(Context context) {
        Intent intent = MyTbaUpdateService.newInstance(context, true, true);
        context.startService(intent);
    }

}
