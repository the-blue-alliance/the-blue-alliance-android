package com.thebluealliance.androidclient.listeners;

import com.thebluealliance.androidclient.gcm.notifications.BaseNotification;

import android.content.Context;
import android.content.Intent;
import android.view.View;

public class GamedayTickerClickListener implements View.OnClickListener {

    private Context context;
    private Intent intent;

    public GamedayTickerClickListener(Context context, BaseNotification notification) {
        this.context = context;
        this.intent = notification.getIntent(context);
    }

    public GamedayTickerClickListener(Context context, Intent intent) {
        this.context = context;
        this.intent = intent;
    }

    @Override
    public void onClick(View v) {
        if (intent != null && context != null) {
            context.startActivity(intent);
        }
    }
}
