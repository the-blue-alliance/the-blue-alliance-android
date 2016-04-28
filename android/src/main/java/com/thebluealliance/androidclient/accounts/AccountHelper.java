package com.thebluealliance.androidclient.accounts;

import com.google.android.gms.common.GooglePlayServicesUtil;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.Utilities;
import com.thebluealliance.androidclient.background.mytba.DisableMyTBA;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;

public class AccountHelper {

    public static final String PREF_MYTBA_ENABLED = "mytba_enabled";
    public static final String PREF_SELECTED_ACCOUNT = "selected_account";

    public static void enableMyTBA(Context context, boolean enabled) {
        Log.d(Constants.LOG_TAG, "Enabling myTBA: " + enabled);
        if (enabled) {
            // enable myTBA
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            prefs.edit().putBoolean(PREF_MYTBA_ENABLED, true).apply();
        } else {
            //disabled myTBA.
            String currentUser = getSelectedAccount(context);
            if (!currentUser.isEmpty()) {
                Log.d(Constants.LOG_TAG, "removing: " + currentUser);
                //Remove all local content and deregister from GCM
                new DisableMyTBA(context).execute(currentUser);
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                prefs.edit().putBoolean(PREF_MYTBA_ENABLED, false).apply();
            }
        }
    }

    public static boolean isMyTBAEnabled(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(PREF_MYTBA_ENABLED, false);
    }

    public static void setSelectedAccount(Context context, String accoutName) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().putString(PREF_SELECTED_ACCOUNT, accoutName).apply();
    }

    @NonNull
    public static String getCurrentUser(SharedPreferences prefs) {
        return prefs.getString(PREF_SELECTED_ACCOUNT, "");
    }

    public static String getSelectedAccount(Context context) {
        if (context == null) return "";
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return getCurrentUser(prefs);
    }

    public static boolean isAccountSelected(Context context) {
        return !getSelectedAccount(context).isEmpty();
    }

    public static String getWebClientId(Context context) {
        return Utilities.readLocalProperty(context, "appspot.webClientId");
    }

    public static String getAndroidClientId(Context context) {
        return Utilities.readLocalProperty(context, "appspot.androidClientId");
    }

    public static String getAudience(Context context) {
        return "server:client_id:" + getWebClientId(context);
    }

    public static boolean registerSystemAccount(Context context, String accountName) {
        // register the account with the system
        AccountManager accountManager = AccountManager.get(context);
        if (accountManager.getAccountsByType(context.getString(R.string.account_type)).length == 0) {
            Account account = new Account(accountName, context.getString(R.string.account_type));
            return accountManager.addAccountExplicitly(account, null, null);
        }

        //account already exists
        return true;
    }

    public static boolean checkGooglePlayServicesAvailable(Activity activity) {
        final int connectionStatusCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);
        if (GooglePlayServicesUtil.isUserRecoverableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(activity, connectionStatusCode);
            return false;
        }
        return true;
    }

    public static void showGooglePlayServicesAvailabilityErrorDialog(final Activity activity,
                                                                     final int connectionStatusCode) {
        final int REQUEST_GOOGLE_PLAY_SERVICES = 0;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Dialog dialog = GooglePlayServicesUtil.getErrorDialog(
                        connectionStatusCode, activity, REQUEST_GOOGLE_PLAY_SERVICES);
                dialog.show();
            }
        });
    }
}
