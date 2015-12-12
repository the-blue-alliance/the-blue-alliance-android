package com.thebluealliance.androidclient.gcm;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;

import com.appspot.tbatv_prod_hrd.tbaMobile.TbaMobile;
import com.appspot.tbatv_prod_hrd.tbaMobile.model.ModelsMobileApiMessagesBaseResponse;
import com.appspot.tbatv_prod_hrd.tbaMobile.model.ModelsMobileApiMessagesRegistrationRequest;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.Utilities;
import com.thebluealliance.androidclient.accounts.AccountHelper;
import com.thebluealliance.androidclient.background.mytba.RegisterGCM;

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

    public static void registerInBackground(Context context) {
        new RegisterGCM(context).execute();
    }

    public static void storeRegistrationId(Context context, String id) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().putString(PROPERTY_GCM_REG_ID, id).apply();
    }

    public static boolean sendRegistrationToBackend(Context context, String gcmId) {
        Log.i(Constants.LOG_TAG, "Registering gcmId " + gcmId);
        GoogleAccountCredential currentCredential = AccountHelper.getSelectedAccountCredential(context);
        try {
            String token = currentCredential.getToken();
        } catch (IOException e) {
            Log.e(Constants.LOG_TAG, "IO Exception while fetching account token for " + currentCredential.getSelectedAccountName());
            e.printStackTrace();
        } catch (GoogleAuthException e) {
            Log.e(Constants.LOG_TAG, "Auth exception while fetching token for " + currentCredential.getSelectedAccountName());
            e.printStackTrace();
        }
        TbaMobile service = AccountHelper.getTbaMobile(currentCredential);
        ModelsMobileApiMessagesRegistrationRequest request = new ModelsMobileApiMessagesRegistrationRequest();
        request.setMobileId(gcmId);
        request.setOperatingSystem(OS_ANDROID);
        request.setName(Build.MANUFACTURER + " " + Build.MODEL);
        request.setDeviceUuid(Utilities.getDeviceUUID(context));

        try {
            ModelsMobileApiMessagesBaseResponse response = service.register(request).execute();
            if (response.getCode() == 200 || response.getCode() == 304) {
                return true;
            } else {
                Log.e(Constants.LOG_TAG, response.getCode() + ":" + response.getMessage());
                return false;
            }
        } catch (IOException e) {
            Log.e(Constants.LOG_TAG, "Error with API call.");
            e.printStackTrace();
        }
        return false;
    }

}
