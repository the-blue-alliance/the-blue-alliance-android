package com.thebluealliance.androidclient.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.thebluealliance.androidclient.TbaLogger;
import com.thebluealliance.androidclient.eventbus.ConnectivityChangeEvent;
import com.thebluealliance.androidclient.helpers.ConnectionDetector;

import org.greenrobot.eventbus.EventBus;

public class ConnectivityChangeReceiver extends BroadcastReceiver {

    /**
     * Receive the system's broadcast that internet connectivity has changed Second, send out a <a
     * href="http://developer.android.com/reference/android/support/v4/content/LocalBroadcastManager.html">Local
     * Broadcast</a> that the current active RefreshableHostActivity can hook into and initiate a
     * refresh
     *
     * @param context Input context
     * @param intent  Sent intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        TbaLogger.i("Received connectivity change intent: " + intent.getAction());

        // If we now have interwebz, send out a local broadcast telling things to refresh
        int connectionStatus;
        if (ConnectionDetector.isConnectedToInternet(context)) {
            connectionStatus = ConnectivityChangeEvent.CONNECTION_FOUND;
        } else {
            connectionStatus = ConnectivityChangeEvent.CONNECTION_LOST;
        }
        EventBus.getDefault().post(new ConnectivityChangeEvent(connectionStatus));
    }
}
