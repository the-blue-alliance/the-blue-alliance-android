package com.thebluealliance.androidclient.listeners;

import android.content.Context;
import android.util.Log;
import android.view.View;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.activities.ViewMatchActivity;

/**
 * File created by phil on 7/18/14.
 */
public class MatchClickListener implements View.OnClickListener {

    Context context;

    public MatchClickListener(Context c){
        context = c;
    }

    @Override
    public void onClick(View v) {
        String matchKey = v.findViewById(R.id.match_title).getTag().toString();
        Log.d(Constants.LOG_TAG, "Match key: " + matchKey);
        context.startActivity(ViewMatchActivity.newInstance(context, matchKey));
    }
}
