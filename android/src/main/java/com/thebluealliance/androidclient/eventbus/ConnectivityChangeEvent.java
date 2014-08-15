package com.thebluealliance.androidclient.eventbus;

/**
 * Created by Nathan on 8/15/2014.
 */
public class ConnectivityChangeEvent {

    public static final int CONNECTION_FOUND = 0;
    public static final int CONNECTION_LOST = 1;

    private int changeType;

    public ConnectivityChangeEvent(int changeType) {
        this.changeType = changeType;
    }

    public int getConnectivityChangeType() {
        return changeType;
    }
}
