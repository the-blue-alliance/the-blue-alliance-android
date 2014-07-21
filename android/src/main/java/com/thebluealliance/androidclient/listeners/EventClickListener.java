package com.thebluealliance.androidclient.listeners;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.activities.TeamAtEventActivity;
import com.thebluealliance.androidclient.activities.ViewEventActivity;
import com.thebluealliance.androidclient.adapters.ListViewAdapter;
import com.thebluealliance.androidclient.listitems.ListElement;

/**
 * Created by phil on 7/21/14.
 */
public class EventClickListener implements AdapterView.OnItemClickListener {

    private Context context;
    private String mTeamKey;

    public EventClickListener(Context c, String teamKey){
        context = c;
        mTeamKey = teamKey;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (!(parent.getAdapter() instanceof ListViewAdapter)) {
            //safety check. Shouldn't ever be tripped unless someone messed up in code somewhere
            Log.w(Constants.LOG_TAG, "Someone done goofed. A ListView adapter doesn't extend ListViewAdapter. Try again...");
            return;
        }
        Object item = ((ListViewAdapter) parent.getAdapter()).getItem(position);
        if (item != null && item instanceof ListElement) {
            // only open up the view event activity if the user actually clicks on a ListElement
            // (as opposed to something inheriting from ListHeader, which shouldn't do anything on user click
            Intent intent;
            String eventKey = ((ListElement) item).getKey();
            if (mTeamKey == null || mTeamKey.isEmpty()) {
                //no team is selected, go to the event details
                intent = ViewEventActivity.newInstance(context, eventKey);
            } else {
                //team is selected, open up the results for that specific team at the event
                intent = TeamAtEventActivity.newInstance(context, eventKey, mTeamKey);
            }
            context.startActivity(intent);
        } else {
            Log.d(Constants.LOG_TAG, "ListHeader clicked. Ignore...");
        }
    }
}
