package com.thebluealliance.androidclient.adapters;

import android.content.Context;

import com.thebluealliance.androidclient.listitems.ListItem;
import com.thebluealliance.androidclient.listitems.StatsListElement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by brycematsuda on 7/17/14.
 */
public class EventStatsFragmentAdapter extends ListViewAdapter {

    public EventStatsFragmentAdapter(Context context, List<ListItem> values) {
        super(context, values);
    }

    /**
     * Sorts event stats based on given stat.
     * @param adapter the fragment adapter whose values will be sorted.
     * @param stat stat to sort by
     */
    public void sortStats(EventStatsFragmentAdapter adapter, String stat) {
        ArrayList<StatsListElement> list = new ArrayList<>();
        list.addAll((ArrayList) adapter.values);

        if (stat.equals("opr")) {
            Collections.sort(list, new Comparator<StatsListElement>() {
                @Override
                public int compare(StatsListElement element1, StatsListElement element2) {
                    return Double.compare(element1.getOpr(), element2.getOpr());
                }
            });
            Collections.reverse(list);
        } else if (stat.equals("dpr")) {
            Collections.sort(list, new Comparator<StatsListElement>() {
                @Override
                public int compare(StatsListElement element1, StatsListElement element2) {
                    return Double.compare(element1.getDpr(), element2.getDpr());
                }
            });
        } else if (stat.equals("ccwm")) {
            Collections.sort(list, new Comparator<StatsListElement>() {
                @Override
                public int compare(StatsListElement element1, StatsListElement element2) {
                    return Double.compare(element1.getCcwm(), element2.getCcwm());
                }
            });
            Collections.reverse(list);
        }

        // Wipe old data and re-add new sorted data
        adapter.clear();
        adapter.addAll(list);

        // Notify data change
        this.notifyDataSetChanged();
    }
}