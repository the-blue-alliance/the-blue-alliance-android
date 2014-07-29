package com.thebluealliance.androidclient.gcm.messages;

import android.content.Context;
import android.os.Bundle;

import com.thebluealliance.androidclient.gcm.GCMHelper;

/**
 * File created by phil on 7/27/14.
 */
public abstract class BasicGCMMessage {

    public static final String MESSAGE_TYPE = "msg_type";
    protected GCMHelper.MSGTYPE type;

    public BasicGCMMessage(GCMHelper.MSGTYPE msgtype){
        this.type = msgtype;
    }

    public abstract Bundle getMessage(Context context);

}
