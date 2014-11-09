package com.thebluealliance.androidclient.gcm;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;

import com.appspot.tbatv_prod_hrd.tbaMobile.TbaMobile;
import com.appspot.tbatv_prod_hrd.tbaMobile.model.ModelsMobileApiMessagesBaseResponse;
import com.appspot.tbatv_prod_hrd.tbaMobile.model.ModelsMobileApiMessagesRegistrationRequest;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.accounts.AccountHelper;

import java.io.IOException;

/**
 * Created by phil on 7/31/14.
 */
public class GCMAuthHelper {

    public static final String OS_ANDROID = "android";
    public static final String PROPERTY_GCM_REG_ID = "gcm_registration_id";
    public static final String REGISTRATION_CHECKSUM = "checksum";


    public static String getRegistrationId(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(PROPERTY_GCM_REG_ID, "");
    }

    public static void registerInBackground(final Activity activity) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    GoogleCloudMessaging gcm = GCMHelper.getGcm(activity);

                    String senderId = GCMHelper.getSenderId(activity);
                    String regid = gcm.register(senderId);

                    Log.d(Constants.LOG_TAG, "Device registered with GCM, ID: " + regid);

                    boolean storeOnServer = GCMAuthHelper.sendRegistrationToBackend(activity, regid);
                    if (storeOnServer) {
                        Log.d(Constants.LOG_TAG, "Storing registration ID");
                        // we had success on the server. Now store locally
                        // Store the registration ID locally, so we don't have to do this again
                        GCMAuthHelper.storeRegistrationId(activity, regid);
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

    public static boolean sendRegistrationToBackend(Activity activity, String gcmId) {
        Log.i(Constants.LOG_TAG, "Registering gcmId " + gcmId);
        GoogleAccountCredential currentCredential = AccountHelper.getSelectedAccountCredential(activity);
        try {
            String token = currentCredential.getToken();
        } catch (IOException e) {
            Log.e(Constants.LOG_TAG, "IO Exception while fetching account token for "+currentCredential.getSelectedAccountName());
            e.printStackTrace();
        } catch (GoogleAuthException e) {
            Log.e(Constants.LOG_TAG, "Auth exception while fetching token for "+currentCredential.getSelectedAccountName());
            e.printStackTrace();
        }
        TbaMobile service = AccountHelper.getTbaMobile(currentCredential);
        ModelsMobileApiMessagesRegistrationRequest request = new ModelsMobileApiMessagesRegistrationRequest();
        request.setMobileId(gcmId);
        request.setOperatingSystem(OS_ANDROID);
        request.setName(Build.MANUFACTURER + " " + Build.MODEL);
        request.setDeviceUuid(Settings.Secure.getString(activity.getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID));

        try {
            ModelsMobileApiMessagesBaseResponse response = service.register(request).execute();
            if(response.getCode() == 200 || response.getCode() == 304){
                return true;
            }else{
                Log.e(Constants.LOG_TAG, response.getCode()+":"+response.getMessage());
                return false;
            }
        } catch (IOException e) {
            Log.e(Constants.LOG_TAG, "Error with API call.");
            e.printStackTrace();
        }
        return false;
    }

}
