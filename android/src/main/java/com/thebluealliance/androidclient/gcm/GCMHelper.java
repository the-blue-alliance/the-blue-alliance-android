package com.thebluealliance.androidclient.gcm;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.Utilities;
import com.thebluealliance.androidclient.gcm.messages.RegistrationMessage;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * File created by phil on 7/27/14.
 */
public class GCMHelper {

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    public static final String PROPERTY_GCM_REG_ID = "gcm_registration_id";
    private static final String GCM_SENDER_FORMAT = "%s@gcm.googleapis.com";

    private static GoogleCloudMessaging gcm;
    private static String senderId;
    private static AtomicInteger msgId = new AtomicInteger();

    public enum MSGTYPE{
        REGISTRATION,
        UPCOMING_MATCH,
        MATCH_SCORE,
        ALLIANCE_SELECTION,
        LEVEL_STARTING,
        SUGGEST_MEDIA;

        public String toString(){
            switch (this){
                case REGISTRATION: return "registration";
                case UPCOMING_MATCH: return "upcoming_match";
                case MATCH_SCORE: return "match_score";
                case ALLIANCE_SELECTION: return "alliance_selection";
                case LEVEL_STARTING: return "starting_comp_level";
                case SUGGEST_MEDIA: return "suggest_media";
            }
            return "";
        }
    }

    public static GoogleCloudMessaging getGcm(Context context){
        return GoogleCloudMessaging.getInstance(context);
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private static boolean checkPlayServices(Activity activity) {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, activity,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(Constants.LOG_TAG, "This device is not supported.");
                activity.finish();
            }
            return false;
        }
        return true;
    }

    public static String getIdOrRegister(Activity activity){
        if(checkPlayServices(activity)) {
            String gcmId = GCMHelper.getRegistrationId(activity);
            if (gcmId.isEmpty()) {
                GCMHelper.registerInBackground(activity);
            }
            return gcmId;
        }else{
            Log.w(Constants.LOG_TAG, "No Play Services. Can't get GCM ID");
            return "";
        }
    }

    /**
     * Gets the current registration ID for application on GCM service.
     * <p>
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     *         registration ID.
     */
    public static String getRegistrationId(Context context) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String registrationId = prefs.getString(PROPERTY_GCM_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(Constants.LOG_TAG, "GCM Registration not found.");
            return "";
        }
        return registrationId;
    }

    public static void registerInBackground(Context c){
        new RegisterGCM(c).execute();
    }

    /**
     * If the app gets updated, we  need to clear the GCM registration ID,
     * as the existing one is not guarateed to continue working
     */
    public static void clearGCMRegistration(Context context){
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().putString(PROPERTY_GCM_REG_ID, "").commit();
    }

    /**
     * Stores the registration ID and app versionCode in the application's
     * {@code SharedPreferences}.
     *
     * @param context application's context.
     * @param regId registration ID
     */
    public static void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_GCM_REG_ID, regId);
        editor.commit();
    }

    /**
     * Sends the registration ID to your server over HTTP, so it can use GCM/HTTP
     * or CCS to send messages to your app.
     */
    public static void sendRegistrationIdToBackend(Context context, String regId) {
        RegistrationMessage message = new RegistrationMessage(regId);
        sendUpstreamMessage(context, message.getMessage(context));
    }

    public static String getSenderId(Context c){
        if(senderId == null){
            senderId = Utilities.readLocalProperty(c, "gcm.senderId");
        }
        return senderId;
    }

    public static void sendUpstreamMessage(final Context c, final Bundle data) {
        new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void[] params) {
                String id = Integer.toString(msgId.incrementAndGet());
                try {
                    getGcm(c).send(
                            String.format(GCM_SENDER_FORMAT, getSenderId(c)),
                            id,
                            data);
                } catch (IOException e) {
                    Log.e(Constants.LOG_TAG, "IO Exception while sending GCM Message");
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
    }

}
