package com.thebluealliance.androidclient.gcm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.thebluealliance.androidclient.TbaLogger;

public class GCMBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        TbaLogger.d("Got GCM Message. " + intent);
        GCMMessageHandler.enqueueWork(context, intent);
    }
}
