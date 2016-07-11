package com.thebluealliance.androidclient.gcm.messages;

import com.thebluealliance.androidclient.gcm.GcmMessageType;

import android.content.Context;
import android.os.Bundle;

public abstract class BasicGCMMessage {

    public static final String MESSAGE_TYPE = "msg_type";
    protected GcmMessageType type;

    public BasicGCMMessage(GcmMessageType msgtype) {
        this.type = msgtype;
    }

    public abstract Bundle getMessage(Context context);

}
