package com.thebluealliance.androidclient.listeners;

import com.thebluealliance.androidclient.activities.TeamAtEventActivity;
import com.thebluealliance.androidclient.activities.ViewEventActivity;
import com.thebluealliance.androidclient.adapters.ListViewAdapter;
import com.thebluealliance.androidclient.helpers.AnalyticsHelper;
import com.thebluealliance.androidclient.helpers.EventTeamHelper;
import com.thebluealliance.androidclient.listitems.ListElement;

import android.content.Context;
import android.content.Intent;
import com.thebluealliance.androidclient.TbaLogger;
import android.view.View;
import android.widget.AdapterView;

public class EventClickListener implements AdapterView.OnItemClickListener, View.OnClickListener {

    private Context context;
    private String mKey;

    public EventClickListener(Context c, String key) {
        context = c;
        mKey = key;
    }

    /**
     * When called from a ListView, assume {@code mKey} is a team key
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (!(parent.getAdapter() instanceof ListViewAdapter)) {
            //safety check. Shouldn't ever be tripped unless someone messed up in code somewhere
            TbaLogger
                    .w("Someone done goofed. A ListView adapter doesn't extend ListViewAdapter. Try again...");
            return;
        }
        Object item = ((ListViewAdapter) parent.getAdapter()).getItem(position);
        if (item != null && item instanceof ListElement) {
            // only open up the view event activity if the user actually clicks on a ListElement
            // (as opposed to something inheriting from ListHeader, which shouldn't do anything on user click
            Intent intent;
            String eventKey = ((ListElement) item).getKey();
            if (mKey == null || mKey.isEmpty()) {
                //no team is selected, go to the event details
                intent = ViewEventActivity.newInstance(context, eventKey);
                AnalyticsHelper.sendClickUpdate(context, "event_click", eventKey, "");
            } else {
                //team is selected, open up the results for that specific team at the event
                intent = TeamAtEventActivity.newInstance(context, eventKey, mKey);
                AnalyticsHelper.sendClickUpdate(context, "team@event_click", EventTeamHelper.generateKey(eventKey, mKey), "");
            }
            context.startActivity(intent);
        } else {
            TbaLogger.d("ListHeader clicked. Ignore...");
        }
    }

    /**
     * When called from a view, from
     * {@link com.thebluealliance.androidclient.fragments.mytba.MyFavoritesFragment} with
     * different listeners
     * required for the different elements in the list
     * Assume {@code mKey} is the event key to open
     */
    @Override
    public void onClick(View v) {
        Intent intent;
        // Open event details
        intent = ViewEventActivity.newInstance(context, mKey);
        AnalyticsHelper.sendClickUpdate(context, "event_click", mKey, "");
        context.startActivity(intent);
    }
}
