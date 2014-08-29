package com.thebluealliance.androidclient.accounts;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.appspot.tba_dev_phil.tbaMobile.TbaMobile;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.AccountPicker;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.Utilities;
import com.thebluealliance.androidclient.background.UpdateMyTBA;
import com.thebluealliance.androidclient.background.mytba.DisableMyTBA;
import com.thebluealliance.androidclient.gcm.GCMHelper;

import java.io.IOException;

/**
 * File created by phil on 7/28/14.
 */
public class AccountHelper {

    public static final String PREF_MYTBA_ENABLED = "mytba_enabled";
    public static final String PREF_SELECTED_ACCOUNT = "selected_account";

    private static final int ACTIVITY_RESULT_FROM_ACCOUNT_SELECTION = 2222;

    public static final JsonFactory JSON_FACTORY = new AndroidJsonFactory();
    public static final HttpTransport HTTP_TRANSPORT = AndroidHttp.newCompatibleTransport();

    public static void enableMyTBA(Activity activity, boolean enabled){
        Log.d(Constants.LOG_TAG, "Enabling myTBA: "+enabled);
        if(enabled && !isAccountSelected(activity)){
            signIn(activity);
        }else if (!enabled && isAccountSelected(activity)){
            //disabled myTBA.
            String currentUser = getSelectedAccount(activity);
            if(!currentUser.isEmpty()){
                Log.d(Constants.LOG_TAG, "removing: "+currentUser);
                //Remove all local content and deregister from GCM
                new DisableMyTBA(activity).execute(currentUser);
            }
        }
    }

    public static void signIn(Activity activity){
        int googleAccounts = AccountHelper.countGoogleAccounts(activity);
        if (googleAccounts == 0) {
            // No accounts registered, nothing to do.
            Log.w(Constants.LOG_TAG, "No google accounts found.");
        } else if (googleAccounts == 1) {
            // If only one account then select it.
            AccountManager am = AccountManager.get(activity);
            Account[] accounts = am.getAccountsByType(GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE);
            if (accounts != null && accounts.length > 0) {
                // Select account and perform authorization check.
                AccountHelper.setSelectedAccount(activity, accounts[0].name);
            }
        } else {
            // More than one Google Account is present, a chooser is necessary.

            // Invoke an {@code Intent} to allow the user to select a Google account.
            Intent accountSelector = AccountPicker.newChooseAccountIntent(null, null,
                    new String[]{GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE}, false,
                    "Select the account to use with The Blue Alliance", null, null, null);
            activity.startActivityForResult(accountSelector,
                    ACTIVITY_RESULT_FROM_ACCOUNT_SELECTION);
        }
    }

    public static void onSignInResult(Activity activity, int requestCode, int resultCode, Intent data){
        Log.d(Constants.LOG_TAG, "Activity result");
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        if (requestCode == ACTIVITY_RESULT_FROM_ACCOUNT_SELECTION && resultCode == activity.RESULT_OK) {
            // This path indicates the account selection activity resulted in the user selecting a
            // Google account and clicking OK.

            // Set the selected account.
            String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
            AccountHelper.setSelectedAccount(activity, accountName);
            prefs.edit().putBoolean(PREF_MYTBA_ENABLED, true).apply();
            GCMHelper.registerGCMIfNeeded(activity);
            new UpdateMyTBA(activity, true).execute();
        }
    }

    public static boolean isMyTBAEnabled(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(PREF_MYTBA_ENABLED, false);
    }

    public static void setSelectedAccount(Context context, String accoutName){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().putString(PREF_SELECTED_ACCOUNT, accoutName).apply();
    }

    public static String getSelectedAccount(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(PREF_SELECTED_ACCOUNT, "");
    }

    public static boolean isAccountSelected(Context context){
        return !getSelectedAccount(context).isEmpty();
    }

    public static GoogleAccountCredential getSelectedAccountCredential(Context context){
        String accountName = getSelectedAccount(context);
        if(accountName == null || accountName.isEmpty()){
            Log.w(Constants.LOG_TAG, "Can't get credential without selected account");
            return null;
        }

        Log.d(Constants.LOG_TAG, "Getting credential for "+accountName);
        GoogleAccountCredential credential = GoogleAccountCredential.usingAudience(context, getAudience(context));
        credential.setSelectedAccountName(accountName);

        return credential;
    }

    public static TbaMobile getTbaMobile(GoogleAccountCredential credential) {
        TbaMobile.Builder tbaMobile = new TbaMobile.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential);
        tbaMobile.setApplicationName("The Blue Alliance");
        return tbaMobile.build();
    }

    public static TbaMobile getAuthedTbaMobile(Context context){
        GoogleAccountCredential currentCredential = AccountHelper.getSelectedAccountCredential(context);
        try {
            String token = currentCredential.getToken();
            Log.d(Constants.LOG_TAG, "token: "+token);
        } catch (IOException e) {
            Log.e(Constants.LOG_TAG, "IO Exception while fetching account token for " + currentCredential.getSelectedAccountName());
            e.printStackTrace();
        } catch (GoogleAuthException e) {
            Log.e(Constants.LOG_TAG, "Auth exception while fetching token for "+currentCredential.getSelectedAccountName());
            e.printStackTrace();
        }
        return AccountHelper.getTbaMobile(currentCredential);
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

    public static int countGoogleAccounts(Context context) {
        AccountManager am = AccountManager.get(context);
        Account[] accounts = am.getAccountsByType(GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE);
        if (accounts == null || accounts.length < 1) {
            return 0;
        } else {
            return accounts.length;
        }
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
