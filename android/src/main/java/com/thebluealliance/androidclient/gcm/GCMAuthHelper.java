package com.thebluealliance.androidclient.gcm;

import com.thebluealliance.androidclient.background.mytba.RegisterGCM;
import com.thebluealliance.androidclient.datafeed.MyTbaDatafeed;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public final class GCMAuthHelper {

    private GCMAuthHelper() {
        // unused
    }

    public static final String OS_ANDROID = "android";
    public static final String PROPERTY_GCM_REG_ID = "gcm_registration_id";
    public static final String REGISTRATION_CHECKSUM = "checksum";


    public static String getRegistrationId(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(PROPERTY_GCM_REG_ID, "");
    }

    public static void registerInBackground(Context context, MyTbaDatafeed datafeed) {
        new RegisterGCM(context, datafeed).execute();
    }

    public static void storeRegistrationId(Context context, String id) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().putString(PROPERTY_GCM_REG_ID, id).apply();
    }
}
