package com.thebluealliance.androidclient.intents;

import android.content.Intent;

/**
 * Created by phil on 7/8/14.
 */
public class ConnectionChangeBroadcast extends Intent {

    public static final int CONNECTION_FOUND = 0;
    public static final int CONNECTION_LOST = 1;
    public static final String CONNECTION_STATUS = "connection_status";

    public static final String ACTION = "com.thebluealliance.androidclient.REFRESH";

    public ConnectionChangeBroadcast(int connectionStatus){
        super();
        setAction(ACTION);
        putExtra(CONNECTION_STATUS, connectionStatus);
    }

}
