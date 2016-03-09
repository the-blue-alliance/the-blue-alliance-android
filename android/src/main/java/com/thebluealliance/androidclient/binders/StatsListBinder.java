package com.thebluealliance.androidclient.binders;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.adapters.EventStatsFragmentAdapter;
import com.thebluealliance.androidclient.adapters.ListViewAdapter;
import com.thebluealliance.androidclient.listitems.ListItem;

import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
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
       if (!isDataBound()) {
            // Always start with Team stats
            setSelectedList(ListPair.LIST0);
           mShowEventStats.setChecked(false);
           mShowTeamStats.setChecked(true);
        }

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
        if (mData == null) {
            return;
        }

        if (group.getCheckedRadioButtonId() == R.id.show_team_stats) {
            setSelectedList(ListPair.LIST0);
        } else if (group.getCheckedRadioButtonId() == R.id.show_event_stats) {
            setSelectedList(ListPair.LIST1);
        }
    }

    @UiThread
    public void setSelectedList(@ListPair.ListOption int option) {
        if (mData != null) {
            MenuItem sortItem = mMenu.findItem(R.id.action_sort_by);
            mData.setSelectedList(option);
            mAdapter.notifyDataSetChanged();
            listView.setClickable(option == ListPair.LIST0);
            if (sortItem != null) {
                sortItem.setVisible(option == ListPair.LIST0);
            }

            if (mData.isEmpty()) {
                bindNoDataView();
            } else {
                listView.setVisibility(View.VISIBLE);
                mNoDataBinder.unbindData();
            }
        }
    }

}
