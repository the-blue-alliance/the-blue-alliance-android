package com.thebluealliance.androidclient.accounts;

import com.thebluealliance.androidclient.DefaultTestRunner;
import com.thebluealliance.androidclient.config.AppConfig;
import com.thebluealliance.androidclient.auth.User;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(DefaultTestRunner.class)
public class AccountControllerTest {

    private static final String TEST_ACCOUNT_TYPE = "test";
    private static final String TEST_ACCOUNT_NAME = "foo@bar.com";
    private static final String TEST_USER_NAME = "Test User";
    private static final String PREF_MYTBA_ENABLED = "mytba_enabled";
    private static final String PREF_SELECTED_ACCOUNT = "selected_account";

    @Mock SharedPreferences mSharedPreferences;
    @Mock SharedPreferences.Editor mEditor;
    @Mock AccountManager mAccountManager;
    @Mock AppConfig mAppConfig;
    @Mock Context mContext;
    @Mock User mUser;

    private AccountController mAccountController;
    private Account mAccount;

    @SuppressLint("CommitPrefEdits")
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mAccount = new Account(TEST_ACCOUNT_NAME, TEST_ACCOUNT_TYPE);
        when(mSharedPreferences.edit()).thenReturn(mEditor);
        when(mUser.getEmail()).thenReturn(TEST_ACCOUNT_NAME);
        when(mUser.getName()).thenReturn(TEST_USER_NAME);
        when(mEditor.putString(anyString(), anyString())).thenReturn(mEditor);
        when(mEditor.putBoolean(anyString(), anyBoolean())).thenReturn(mEditor);
        mAccountController = new AccountController(mSharedPreferences,
                                                   mAccountManager,
                                                   mAppConfig,
                                                   TEST_ACCOUNT_TYPE);
    }

    @Test
    public void testGetAccountNoneAvailable() {
        when(mAccountManager.getAccountsByType(TEST_ACCOUNT_TYPE))
                .thenReturn(new Account[]{});
        when(mSharedPreferences.getString(PREF_SELECTED_ACCOUNT, "")).thenReturn(TEST_ACCOUNT_NAME);
        Account selectedAccount = mAccountController.getCurrentAccount();
        assertNull(selectedAccount);
    }

    @Test
    public void testGetAccountNoneSelected() {
        when(mAccountManager.getAccountsByType(TEST_ACCOUNT_TYPE))
                .thenReturn(new Account[]{mAccount});
        when(mSharedPreferences.getString(PREF_SELECTED_ACCOUNT, "")).thenReturn(null);
        Account selectedAccount = mAccountController.getCurrentAccount();
        assertNull(selectedAccount);
    }

    @Test
    public void testGetAccount() {
        when(mAccountManager.getAccountsByType(TEST_ACCOUNT_TYPE))
                .thenReturn(new Account[]{mAccount});
        when(mSharedPreferences.getString(PREF_SELECTED_ACCOUNT, "")).thenReturn(TEST_ACCOUNT_NAME);
        Account selectedAccount = mAccountController.getCurrentAccount();
        assertEquals(selectedAccount, mAccount);
    }

    /**
     * Ensure that we do all the things we have to do when an account connects
     *  - enable myTBA
     *  - register the system account
     *  - set the selected account name
     *  - start the service to register for GCM
     *  - start the service to load myTBA data
     */
    @Test
    public void testOnAccountConnect() {
        when(mAccountManager.getAccountsByType(TEST_ACCOUNT_TYPE)).thenReturn(new Account[]{});
        mAccountController.onAccountConnect(mContext, mUser);

        verify(mEditor).putBoolean(PREF_MYTBA_ENABLED, true);
        verify(mEditor).putString(PREF_SELECTED_ACCOUNT, TEST_ACCOUNT_NAME);
        verify(mAccountManager).addAccountExplicitly(any(Account.class), eq(null), eq(null));
        verify(mContext, times(2)).startService(any(Intent.class));
    }

    @Test
    public void testGetWebClientId() {
        mAccountController.getWebClientId();
        verify(mAppConfig).getString("appspot_webClientId");
    }
}