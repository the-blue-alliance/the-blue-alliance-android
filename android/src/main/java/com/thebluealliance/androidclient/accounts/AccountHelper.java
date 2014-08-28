package com.thebluealliance.androidclient.accounts;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.appspot.tba_dev_phil.tbaMobile.TbaMobile;
import com.appspot.tba_dev_phil.tbaMobile.model.ModelsMobileApiMessagesBaseResponse;
import com.appspot.tba_dev_phil.tbaMobile.model.ModelsMobileApiMessagesFavoriteMessage;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.Utilities;

import java.io.IOException;

/**
 * File created by phil on 7/28/14.
 */
public class AccountHelper {
    public static final String PREF_SELECTED_ACCOUNT = "selected_account";


    public static final JsonFactory JSON_FACTORY = new AndroidJsonFactory();
    public static final HttpTransport HTTP_TRANSPORT = AndroidHttp.newCompatibleTransport();

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

    public static boolean addFavorite(GoogleAccountCredential credential, String modelKey) {
        TbaMobile service = AccountHelper.getTbaMobile(credential);
        ModelsMobileApiMessagesFavoriteMessage request = new ModelsMobileApiMessagesFavoriteMessage();
        request.setModelKey(modelKey);

        try {
            ModelsMobileApiMessagesBaseResponse response = service.favorites().add(request).execute();
            if (response.getCode() == 200) {
                return true;
            } else {
                Log.e(Constants.LOG_TAG, response.getMessage());
                return false;
            }
        } catch (IOException e) {
            Log.e(Constants.LOG_TAG, "Error with API call.");
            e.printStackTrace();
        }
        return false;
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
