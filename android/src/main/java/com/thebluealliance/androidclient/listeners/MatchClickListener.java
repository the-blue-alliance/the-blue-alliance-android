package com.thebluealliance.androidclient.listeners;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.activities.ViewMatchActivity;
import com.thebluealliance.androidclient.helpers.AnalyticsHelper;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;

public class MatchClickListener implements View.OnClickListener {

    Context context;

    public MatchClickListener(Context c) {
        context = c;
    }

    @Override
    public void onClick(View v) {
        String matchKey = v.findViewById(R.id.match_title).getTag().toString();
        Log.d(Constants.LOG_TAG, "Match key clicked: " + matchKey);
        Intent intent = ViewMatchActivity.newInstance(context, matchKey);
        AnalyticsHelper.sendClickUpdate(context, "match_click", matchKey, "");
        context.startActivity(intent);
    }
}
