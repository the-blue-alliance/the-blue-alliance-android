package com.thebluealliance.androidclient.adapters;

import android.content.Context;

import com.thebluealliance.androidclient.binders.ListPair;
import com.thebluealliance.androidclient.comparators.StatListElementComparator;
import com.thebluealliance.androidclient.listitems.ListItem;

import java.util.Collections;

public class EventStatsFragmentAdapter extends ListViewAdapter {

    private final ListPair<ListItem> mListPair;

    public EventStatsFragmentAdapter(Context context, ListPair<ListItem> values) {
        super(context, values);
        mListPair = values;
    }

    public void setSelectedList(@ListPair.ListOption int option) {
        mListPair.setSelectedList(option);
        notifyDataSetChanged();
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