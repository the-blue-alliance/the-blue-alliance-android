package com.thebluealliance.androidclient.intents;

import android.content.Intent;

/**
 * Created by phil on 7/8/14.
 */
public class ConnectionChangeBroadcast extends Intent {

    public static final String ACTION = "com.thebluealliance.androidclient.REFRESH";

    public ConnectionChangeBroadcast(){
        super();
        setAction(ACTION);
    }

}
