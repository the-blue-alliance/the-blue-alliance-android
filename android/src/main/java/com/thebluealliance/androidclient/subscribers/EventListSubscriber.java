package com.thebluealliance.androidclient.subscribers;

import android.content.Context;

import com.thebluealliance.androidclient.adapters.ListViewAdapter;
import com.thebluealliance.androidclient.listitems.ListItem;
import com.thebluealliance.androidclient.models.Event;

import java.util.ArrayList;
import java.util.List;

/**
 * Bind a list of events to a {@link ListViewAdapter}
 */
public class EventListSubscriber extends BaseAPISubscriber<List<Event>, ListViewAdapter> {

    public EventListSubscriber(Context context) {
        super();
        mDataToBind = new ListViewAdapter(context, new ArrayList<>());
    }

    @Override
    public void parseData() {
        mDataToBind.values.clear();
        for (int i=0; i < mAPIData.size(); i++) {
            Event event = mAPIData.get(i);
            if (event == null) {
                continue;
            }
            ListItem item = event.render();
            if (item == null) {
                continue;
            }
            mDataToBind.values.add(item);
        }
    }
}
