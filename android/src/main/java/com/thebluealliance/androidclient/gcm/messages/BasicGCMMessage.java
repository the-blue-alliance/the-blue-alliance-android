package com.thebluealliance.androidclient.gcm.messages;

import com.thebluealliance.androidclient.gcm.GCMHelper;

import android.content.Context;
import android.os.Bundle;

public abstract class BasicGCMMessage {

    public static final String MESSAGE_TYPE = "msg_type";
    protected GCMHelper.MSGTYPE type;

    public BasicGCMMessage(GCMHelper.MSGTYPE msgtype) {
        this.type = msgtype;
    }

    public abstract Bundle getMessage(Context context);

}
