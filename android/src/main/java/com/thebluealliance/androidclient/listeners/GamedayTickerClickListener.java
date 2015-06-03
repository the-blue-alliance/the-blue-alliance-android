package com.thebluealliance.androidclient.listeners;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.thebluealliance.androidclient.gcm.notifications.BaseNotification;

/**
 * Created by phil on 4/16/15.
 */
public class GamedayTickerClickListener implements View.OnClickListener {

    private Context context;
    private BaseNotification notification;

    public GamedayTickerClickListener(Context context, BaseNotification notification) {
        this.context = context;
        this.notification = notification;
    }


    @Override
    public void onClick(View v) {
        Intent intent = notification.getIntent(context);
        if (intent != null) {
            context.startActivity(intent);
        }
    }
}
