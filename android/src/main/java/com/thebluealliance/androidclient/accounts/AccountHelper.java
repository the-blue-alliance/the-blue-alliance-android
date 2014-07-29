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
import com.thebluealliance.androidclient.Utilities;
import com.thebluealliance.androidclient.datafeed.HTTP;
import com.thebluealliance.androidclient.datafeed.JSONManager;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * File created by phil on 7/28/14.
 */
public class AccountHelper {

    private static final String USER_INFO_REQUEST = "https://www.googleapis.com/oauth2/v2/userinfo";
    private static final String USER_ID = "user_id", USER_EMAIL = "user_email", USER_NAME = "user_name", PIC_URL = "user_pic";

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
                    result.addProperty(USER_EMAIL, accountName);
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
                        userEmail = result.get(USER_EMAIL).getAsString(),
                        userPicUrl = result.get("picture").getAsString();
                Log.d(Constants.LOG_TAG, "Got user id: " + id);
                prefs.edit()    .putString(USER_ID, id)
                                .putString(USER_NAME, userName)
                                .putString(USER_EMAIL, userEmail)
                                .putString(PIC_URL, userPicUrl)
                                .commit();
            }
        }

    }

    protected static String getClientId(Context context){
        if(Utilities.isDebuggable()){
            return Utilities.readLocalProperty(context, "gms.clientId.debug");
        }else{
            return Utilities.readLocalProperty(context, "gms.clientId");
        }
    }
}
