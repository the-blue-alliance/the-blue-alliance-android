package com.thebluealliance.androidclient.binders;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.adapters.EventStatsFragmentAdapter;
import com.thebluealliance.androidclient.adapters.ListViewAdapter;
import com.thebluealliance.androidclient.listitems.ListItem;

import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.util.List;

import butterknife.Bind;

public class StatsListBinder extends ListViewBinder implements RadioGroup.OnCheckedChangeListener {

    @Bind(R.id.stats_type_selector) RadioGroup mSelectorGroup;
    @Bind(R.id.show_event_stats) RadioButton mShowEventStats;
    @Bind(R.id.show_team_stats) RadioButton mShowTeamStats;

    private ListPair<ListItem> mData;
    private Menu mMenu;

    public void setMenu(Menu menu) {
        mMenu = menu;
    }

    @Override
    public void updateData(@Nullable List<ListItem> data) {
        mSelectorGroup.setVisibility(View.VISIBLE);
        mSelectorGroup.setOnCheckedChangeListener(this);

        /** Call this last, it calls {@link #setDataBound(boolean)} when done */
        super.updateData(data);
    }

    @Override
    public ListViewAdapter newAdapter(List<ListItem> data) {
        mData = (ListPair<ListItem>) data;
        return new EventStatsFragmentAdapter(mActivity, mData);
    }

    @Override
    protected void replaceDataInAdapter(List<ListItem> data) {
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        MenuItem sortItem = mMenu.findItem(R.id.action_sort_by);
        if (group.getCheckedRadioButtonId() == R.id.show_team_stats) {
            mData.setSelectedList(ListPair.LIST0);
            mAdapter.notifyDataSetChanged();
            listView.setClickable(true);
            if (sortItem != null) {
                sortItem.setVisible(true);
            }
        } else if (group.getCheckedRadioButtonId() == R.id.show_event_stats) {
            mData.setSelectedList(ListPair.LIST1);
            mAdapter.notifyDataSetChanged();
            listView.setClickable(false);
            if (sortItem != null) {
                sortItem.setVisible(false);
            }
        }
    }
}
