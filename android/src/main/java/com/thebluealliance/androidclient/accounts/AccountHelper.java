package com.thebluealliance.androidclient.accounts;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.GooglePlayServicesAvailabilityException;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.Scopes;
import com.google.gson.JsonObject;
import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.datafeed.HTTP;
import com.thebluealliance.androidclient.datafeed.JSONManager;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * File created by phil on 7/28/14.
 */
public class AccountHelper {

    private static final String PREF_ACTIVE_ACCOUNT = "chosen_account";
    private static final String PREF_AUTH_TOKEN = "auth_token_%s";
    private static final String PREF_PLUS_ID = "plus_profile_id_%s";
    private static final String PREF_PLUS_NAME = "plus_name_%s";
    private static final String PREF_PLUS_PIC = "plus_image_url_%s";
    private static final String PREF_PLUS_COVER_URL = "plus_cover_url_%s";
    private static final String PREF_GCM_KEY = "gcm_key_%s";

    private static final String USER_INFO_REQUEST = "https://www.googleapis.com/oauth2/v2/userinfo";
    private static final String ACCOUNT_NAME = "account_name";

    public static void storeAccountId(Activity activity, String accountName){
        new IdGetter(activity).execute(accountName);
    }

    private static class IdGetter extends AsyncTask<String, Void, JsonObject>{

        static final String OAUTH_SCOPE_FORMAT = "oauth2:%s";
        final private List<String> SCOPES = Arrays.asList(Scopes.PLUS_LOGIN, Scopes.PROFILE);
        static final int REQUEST_CODE = 1124;
        Activity activity;

        public IdGetter(Activity activity){
            super();
            this.activity = activity;
        }

        @Override
        protected JsonObject doInBackground(String... params) {
            String accountName = params[0];
            String token = null;
            String scope = String.format(OAUTH_SCOPE_FORMAT, TextUtils.join(" ", SCOPES));
            Log.d(Constants.LOG_TAG, "Fetching token for "+accountName);
            try {
                token = GoogleAuthUtil.getToken(activity,
                        accountName,
                        scope,
                        new Bundle());
                Log.d(Constants.LOG_TAG, "Got oauth token: "+token);
                if(token != null) {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Authorization","Bearer "+token);
                    JsonObject result = JSONManager.getasJsonObject(HTTP.GET(String.format(USER_INFO_REQUEST), headers));
                    result.addProperty(ACCOUNT_NAME, accountName);
                    setCurrentUser(activity, accountName);
                    return result;
                }

            } catch (GooglePlayServicesAvailabilityException playEx) {
                Dialog dialog = GooglePlayServicesUtil.getErrorDialog(
                        playEx.getConnectionStatusCode(),
                        activity,
                        REQUEST_CODE);
                dialog.show();
            } catch (UserRecoverableAuthException recoverableException) {
                Log.w(Constants.LOG_TAG, "recoverable: ");
                recoverableException.printStackTrace();
                Intent recoveryIntent = recoverableException.getIntent();
                activity.startActivityForResult(recoveryIntent, REQUEST_CODE);
            } catch (GoogleAuthException authEx) {
                // This is likely unrecoverable.
                Log.e(Constants.LOG_TAG, "Unrecoverable authentication exception: " + authEx.getMessage(), authEx);
            } catch (IOException ioEx) {
                Log.i(Constants.LOG_TAG, "transient error encountered: " + ioEx.getMessage());
                //TODO should do exponential back off here
            }

            return null;
        }

        @Override
        protected void onPostExecute(JsonObject result) {
            super.onPostExecute(result);
            if(result != null) {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
                String  id = result.get("id").getAsString(),
                        userName = result.get("name").getAsString(),
                        accountName = result.get(ACCOUNT_NAME).getAsString(),
                        userPicUrl = result.get("picture").getAsString();
                Log.d(Constants.LOG_TAG, "Got user id: " + id);
                prefs.edit()    .putString(String.format(PREF_PLUS_ID, accountName), id)
                                .putString(String.format(PREF_PLUS_NAME, accountName), userName)
                                .putString(String.format(PREF_PLUS_PIC, accountName), userPicUrl)
                                .commit();
            }
        }

    }

    public static void setCurrentUser(Context context, String accountName){
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(PREF_ACTIVE_ACCOUNT, accountName).commit();
    }

    public static String getCurrentUser(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getString(PREF_ACTIVE_ACCOUNT, "");
    }

    public static String getGCMKey(Context context){
        String currentUser = getCurrentUser(context);
        if(TextUtils.isEmpty(currentUser)){
            Log.e(Constants.LOG_TAG, "No current user: can't get GCM Key");
            return null;
        }
        return getGCMKey(context, currentUser);
    }

    public static String getGCMKey(Context context, String accountName){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String gcmKey = prefs.getString(String.format(PREF_GCM_KEY, accountName), null);

        // if there is no current GCM key, generate a new random one
        if (TextUtils.isEmpty(gcmKey)) {
            gcmKey = UUID.randomUUID().toString();
            Log.d(Constants.LOG_TAG, "No GCM key on account " + accountName + ". Generating random one.");
            setGcmKey(context, accountName, gcmKey);
        }

        return gcmKey;
    }

    public static void setGcmKey(Context context, String accountName, String gcmKey) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().putString(String.format(PREF_GCM_KEY, accountName), gcmKey).commit();
        // TODO store this in user's Google Drive application data
        // https://developers.google.com/drive/web/appdata
        Log.d(Constants.LOG_TAG, "Created secret GCM key for account " + accountName);
    }
}
