package com.thebluealliance.androidclient.gcm;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import androidx.legacy.content.WakefulBroadcastReceiver;

import com.thebluealliance.androidclient.TbaLogger;

public class GCMBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        TbaLogger.d("Got GCM Message. " + intent);
        GCMMessageHandler.enqueueWork(context, intent);
    }
}
