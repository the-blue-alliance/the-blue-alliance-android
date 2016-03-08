package com.thebluealliance.androidclient.binders;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.adapters.EventStatsFragmentAdapter;
import com.thebluealliance.androidclient.adapters.ListViewAdapter;
import com.thebluealliance.androidclient.listitems.ListItem;

import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.util.List;

import butterknife.Bind;

public class StatsListBinder extends ListViewBinder {

    @Bind(R.id.stats_type_selector) RadioGroup mSelectorGroup;
    @Bind(R.id.show_event_stats) RadioButton mShowEventStats;
    @Bind(R.id.show_team_stats) RadioButton mShowTeamStats;

    private ListPair<ListItem> mData;

    @Override
    public ListViewAdapter newAdapter(List<ListItem> data) {
        mData = (ListPair<ListItem>) data;
        return new EventStatsFragmentAdapter(mActivity, mData);
    }

    @Override
    protected void replaceDataInAdapter(List<ListItem> data) {
        mAdapter.notifyDataSetChanged();
    }
}
