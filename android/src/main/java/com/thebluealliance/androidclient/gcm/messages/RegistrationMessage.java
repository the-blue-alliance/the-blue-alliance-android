package com.thebluealliance.androidclient.gcm.messages;

import android.os.Bundle;

import com.thebluealliance.androidclient.gcm.GCMHelper;

/**
 * File created by phil on 7/27/14.
 */
public class RegistrationMessage extends BasicGCMMessage {

    public static final String GCM_ID = "gcm_id",
        DEVICE_ID = "device_id",
        USER_ID = "user_id",
        OS = "operating_system",
            ANDROID = "android";
    private String gcmId;

    public RegistrationMessage(String gcmId){
        super(GCMHelper.MSGTYPE.REGISTRATION);
        this.gcmId = gcmId;
    }

    @Override
    public Bundle getMessage() {
        Bundle data = new Bundle();
        data.putString(BasicGCMMessage.MESSAGE_TYPE, GCMHelper.MSGTYPE.REGISTRATION.toString());
        data.putString(GCM_ID, gcmId);
        data.putString(DEVICE_ID, ""); // TODO figure out a good way to do this
        data.putString(USER_ID, ""); // TODO use registered Google Account (done at beginning) and get Google Account ID
        data.putString(OS, ANDROID);
        return data;
    }
}
