package com.thebluealliance.androidclient.gcm;

import android.content.Context;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.thebluealliance.androidclient.Utilities;

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

}
