package com.thebluealliance.androidclient.gcm;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.Utilities;
import com.thebluealliance.androidclient.accounts.AccountHelper;

/**
 * File created by phil on 7/27/14.
 */
public class GCMHelper {

    private static String senderId;

    public enum MSGTYPE {
        REGISTRATION,
        UPCOMING_MATCH,
        MATCH_SCORE,
        ALLIANCE_SELECTION,
        LEVEL_STARTING,
        SUGGEST_MEDIA;

        public String toString() {
            switch (this) {
                case REGISTRATION:
                    return "registration";
                case UPCOMING_MATCH:
                    return "upcoming_match";
                case MATCH_SCORE:
                    return "match_score";
                case ALLIANCE_SELECTION:
                    return "alliance_selection";
                case LEVEL_STARTING:
                    return "starting_comp_level";
                case SUGGEST_MEDIA:
                    return "suggest_media";
            }
            return "";
        }
    }

    public static GoogleCloudMessaging getGcm(Context context) {
        return GoogleCloudMessaging.getInstance(context);
    }

    public static String getSenderId(Context c) {
        if (senderId == null) {
            senderId = Utilities.readLocalProperty(c, "gcm.senderId");
        }
        return senderId;
    }

    public static void registerGCMIfNeeded(Activity activity){
        if (!AccountHelper.checkGooglePlayServicesAvailable(activity)) {
            Log.w(Constants.LOG_TAG, "Google Play Services unavailable. Can't register with GCM");
            return;
        }
        final String registrationId = GCMAuthHelper.getRegistrationId(activity);
        if (TextUtils.isEmpty(registrationId)) {
            // GCM has not yet been registered on this device
            Log.d(Constants.LOG_TAG, "GCM is not currently registered. Registering....");
            GCMAuthHelper.registerInBackground(activity);
        }
    }

}
