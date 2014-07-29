package com.thebluealliance.androidclient.gcm.messages;

import android.content.Context;
import android.os.Bundle;

import com.thebluealliance.androidclient.Utilities;
import com.thebluealliance.androidclient.accounts.AccountHelper;
import com.thebluealliance.androidclient.gcm.GCMHelper;

/**
 * File created by phil on 7/27/14.
 */
public class RegistrationMessage extends BasicGCMMessage {

    public static final String
            GCM_ID = "gcm_id",
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
    public Bundle getMessage(Context context) {
        String userId = AccountHelper.getUserId(context);
        if(!userId.isEmpty()) {
            Bundle data = new Bundle();
            data.putString(BasicGCMMessage.MESSAGE_TYPE, GCMHelper.MSGTYPE.REGISTRATION.toString());
            data.putString(GCM_ID, gcmId);
            data.putString(DEVICE_ID, Utilities.getUUID());
            data.putString(USER_ID, userId);
            data.putString(OS, ANDROID);
            return data;
        }else{
            //no selected account. We can't send a message
            return null;
        }
    }
}
