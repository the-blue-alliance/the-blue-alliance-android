package com.thebluealliance.androidclient.listeners;

import com.google.gson.JsonObject;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.helpers.WebcastHelper;
import com.thebluealliance.androidclient.types.WebcastType;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.view.View;

public class WebcastClickListener implements View.OnClickListener {

    private final Context mContext;
    private final String mEventKey;
    private final WebcastType mType;
    private final JsonObject mParams;
    private final int mNumber;

    public WebcastClickListener(Context context, String eventKey, WebcastType type, JsonObject params, int number) {
        mContext = context;
        mEventKey = eventKey;
        mType = type;
        mParams = params;
        mNumber = number;
    }

    @Override
    public void onClick(View v) {
        Intent intent = WebcastHelper.getIntentForWebcast(mContext, mEventKey, mType, mParams, mNumber);
        try {
            mContext.startActivity(intent);
        } catch (ActivityNotFoundException ex) {
            // Unable to find an activity to handle the webcast
            // Fall back by just opening Gameday in browser
            String url = mContext.getString(R.string.webcast_gameday_pattern, mEventKey, mNumber);
            Intent gamedayIntent = WebcastHelper.getWebIntentForUrl(url);
            mContext.startActivity(gamedayIntent);
        }
    }
}
