package com.thebluealliance.androidclient.adapters;

import android.content.Context;

import com.thebluealliance.androidclient.comparators.StatListElementComparator;
import com.thebluealliance.androidclient.listitems.ListItem;

import java.util.Collections;
import java.util.List;

public class EventStatsFragmentAdapter extends ListViewAdapter {

    public EventStatsFragmentAdapter(Context context, List<ListItem> values) {
        super(context, values);
    }

    /**
     * Sorts event stats based on given stat.
     *
     * @param stat stat to sort by
     */
    public void sortStats(String stat) {
        Collections.sort(values, new StatListElementComparator(stat));

        // Notify data change
        notifyDataSetChanged();
    }
}