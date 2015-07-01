package com.thebluealliance.androidclient.binders;

import com.thebluealliance.androidclient.adapters.EventStatsFragmentAdapter;
import com.thebluealliance.androidclient.adapters.ListViewAdapter;
import com.thebluealliance.androidclient.listitems.ListItem;

import java.util.List;

public class StatsListBinder extends ListviewBinder {

    @Override
    public ListViewAdapter newAdapter(List<ListItem> data) {
        return new EventStatsFragmentAdapter(mActivity, data);
    }
}
