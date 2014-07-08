package com.thebluealliance.androidclient.intents;

import android.content.Intent;

/**
 * Created by phil on 7/8/14.
 */
public class RefreshBroadcast extends Intent {

    public static final String ACTION = "com.thebluealliance.androidclient.REFRESH";

    public RefreshBroadcast(){
        super();
        setAction(ACTION);
    }

}
