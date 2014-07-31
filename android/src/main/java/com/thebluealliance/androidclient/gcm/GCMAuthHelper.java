package com.thebluealliance.androidclient.gcm;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.gson.JsonObject;
import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.Utilities;
import com.thebluealliance.androidclient.accounts.AccountHelper;
import com.thebluealliance.androidclient.datafeed.HTTP;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by phil on 7/31/14.
 */
public class GCMAuthHelper {

    public static final String GCM_REGISTER_ENDPOINT = ""; // TODO make this server side

    public static final String PROPERTY_GCM_REG_ID = "gcm_registration_id";
    public static final String PROPERTY_GCM_KEY = "gcm_key";
    public static final String REGISTRATION_CHECKSUM = "checksum";


    public static String getRegistrationId(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(PROPERTY_GCM_REG_ID, "");
    }

    public void registerInBackground(final Context context) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    GoogleCloudMessaging gcm = GCMHelper.getGcm(context);

                    String senderId = GCMHelper.getSenderId(context);
                    String regid = gcm.register(senderId);

                    Log.d(Constants.LOG_TAG, "Device registered with GCM, ID: " + regid);

                    // Store the registration ID locally, so we don't have to do this again
                    GCMAuthHelper.storeRegistrationId(context, regid);

                    String gcmKey = AccountHelper.getGCMKey(context);

                    GCMAuthHelper.sendRegistrationToBackend(context, regid, gcmKey);
                } catch (IOException ex) {
                    Log.e(Constants.LOG_TAG, "Error registering gcm:" + ex.getMessage());
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return null;
            }
        }.execute();
    }

    public static void storeRegistrationId(Context context, String id) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().putString(PROPERTY_GCM_REG_ID, id).commit();
    }

    public static boolean sendRegistrationToBackend(Context context, String gcmId, String gcmKey) {
        Log.i(Constants.LOG_TAG, "Registering gcmId " + gcmId);
        JsonObject requestParams = new JsonObject();
        requestParams.addProperty(PROPERTY_GCM_REG_ID, gcmId);
        requestParams.addProperty(PROPERTY_GCM_KEY, gcmKey);

        Map<String, String> headers = new HashMap<>();
        headers.put(REGISTRATION_CHECKSUM, GCMHelper.requestChecksum(context, requestParams));


        HTTP.POST(GCM_REGISTER_ENDPOINT, headers, requestParams);

        // TODO check for error and do exponential backoff

        return true;
    }

}
