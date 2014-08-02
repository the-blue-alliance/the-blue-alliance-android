package com.thebluealliance.androidclient.gcm;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.gson.JsonObject;
import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.accounts.AccountHelper;
import com.thebluealliance.androidclient.datafeed.HTTP;
import com.thebluealliance.androidclient.datafeed.TBAv2;

import org.apache.http.HttpResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by phil on 7/31/14.
 */
public class GCMAuthHelper {

    public static final String PROPERTY_GCM_REG_ID = "gcm_registration_id";
    public static final String PROPERTY_GCM_KEY = "gcm_key";
    public static final String REGISTRATION_CHECKSUM = "checksum";


    public static String getRegistrationId(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(PROPERTY_GCM_REG_ID, "");
    }

    public static void registerInBackground(final Context context, final GoogleApiClient driveClient) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    GoogleCloudMessaging gcm = GCMHelper.getGcm(context);

                    String senderId = GCMHelper.getSenderId(context);
                    String regid = gcm.register(senderId);

                    Log.d(Constants.LOG_TAG, "Device registered with GCM, ID: " + regid);

                    String gcmKey = AccountHelper.getGCMKey(context, driveClient);

                    boolean storeOnServer = GCMAuthHelper.sendRegistrationToBackend(context, regid, gcmKey);
                    if(storeOnServer){
                        // we had success on the server. Now store locally
                        // Store the registration ID locally, so we don't have to do this again
                        GCMAuthHelper.storeRegistrationId(context, regid);
                    }
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
        Log.i(Constants.LOG_TAG, "Using secret: "+gcmKey);
        JsonObject requestParams = new JsonObject();
        requestParams.addProperty(PROPERTY_GCM_REG_ID, gcmId);
        requestParams.addProperty(PROPERTY_GCM_KEY, gcmKey);

        Map<String, String> headers = new HashMap<>();
        headers.put(REGISTRATION_CHECKSUM, GCMHelper.requestChecksum(context, requestParams));

        String endpoint = TBAv2.getGCMEndpoint(context, TBAv2.GCM_ENDPOINT.REGISTER);
        HttpResponse response = HTTP.postResponse(endpoint, headers, requestParams);
        Log.d(Constants.LOG_TAG, "Result code from registration request: "+response.getStatusLine().getStatusCode());
        Log.d(Constants.LOG_TAG, HTTP.dataFromResponse(response));
        // TODO check for error and do exponential backoff

        return response.getStatusLine().getStatusCode() == 200;
    }

}
