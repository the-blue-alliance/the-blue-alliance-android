package com.thebluealliance.androidclient.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.datafeed.ConnectionDetector;
import com.thebluealliance.androidclient.intents.RefreshBroadcast;

/**
 * Created by phil on 7/8/14.
 */
public class ConnectivityChangeReceiver extends BroadcastReceiver {

    /**
     * Receive the system's broadcast that internet connectivity has changed
     * Second, send out a <a href="http://developer.android.com/reference/android/support/v4/content/LocalBroadcastManager.html">Local Broadcast</a> that the current active
     * RefreshableHostActivity can hook into and initiate a refresh
     * @param context Input context
     * @param intent Sent intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(Constants.LOG_TAG, "Received connectivity change intent: "+intent.getAction());

        // if we now have interwebz, send out a local broadcast telling things to refresh
        if(ConnectionDetector.isConnectedToInternet(context)) {
            LocalBroadcastManager.getInstance(context).sendBroadcast(new RefreshBroadcast());
        }
    }
}
