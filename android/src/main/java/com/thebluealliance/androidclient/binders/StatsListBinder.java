package com.thebluealliance.androidclient.binders;

import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.adapters.EventStatsFragmentAdapter;
import com.thebluealliance.androidclient.adapters.ListViewAdapter;
import com.thebluealliance.androidclient.listitems.ListItem;

import java.util.List;

import butterknife.BindView;

public class StatsListBinder extends ListViewBinder implements RadioGroup.OnCheckedChangeListener {

    @BindView(R.id.stats_type_selector) RadioGroup mSelectorGroup;
    @BindView(R.id.show_event_stats) RadioButton mShowEventStats;
    @BindView(R.id.show_team_stats) RadioButton mShowTeamStats;

    private ListPair<ListItem> mData;
    private Menu mMenu;
    private @ListPair.ListOption int mSelectedList;

    public StatsListBinder() {
        mSelectedList = ListPair.LIST0;
    }

    public void setMenu(Menu menu) {
        mMenu = menu;
        syncSortMenuItemState(mSelectedList);
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
        mData = ((ListPair<ListItem>) data).copyOf();
        return new EventStatsFragmentAdapter(mActivity, mData);
    }

    @Override
    protected void replaceDataInAdapter(List<ListItem> data) {
        mData.replaceAll((ListPair<ListItem>) data);
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
        mSelectedList = option;
        if (mData != null) {
            mData.setSelectedList(option);
            mAdapter.notifyDataSetChanged();
            listView.setClickable(option == ListPair.LIST0);
            syncSortMenuItemState(option);

            if (mData.isEmpty()) {
                bindNoDataView();
            } else {
                listView.setVisibility(View.VISIBLE);
                mNoDataBinder.unbindData();
            }
        }
    }

    private void syncSortMenuItemState(@ListPair.ListOption int option) {
        if (mMenu != null) {
            MenuItem sortItem = mMenu.findItem(R.id.action_sort_by);
            if (sortItem != null) {
                sortItem.setVisible(option == ListPair.LIST0);
            }
        }
    }

}
