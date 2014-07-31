package com.thebluealliance.androidclient.gcm;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.gson.JsonObject;
import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.Utilities;

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
    public static boolean checkPlayServices(Activity activity) {
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

    public static String requestChecksum(Context context, JsonObject data){
        String secret = Utilities.readLocalProperty(context, "gcm.registrationSecret");
        String requestData = data.toString();

        // TODO salt & hash
        return "";
    }

}
