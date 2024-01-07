package com.thebluealliance.androidclient.binders;

import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;

import com.thebluealliance.androidclient.TbaLogger;
import com.thebluealliance.androidclient.adapters.ListViewAdapter;
import com.thebluealliance.androidclient.databinding.ListViewWithSpinnerBinding;
import com.thebluealliance.androidclient.listitems.ListItem;

import java.util.ArrayList;
import java.util.List;

public class ListViewBinder extends AbstractListViewBinder<ListViewWithSpinnerBinding, ListItem, ListViewAdapter> {

    @Override
    protected ListView getList() {
        return mBinding.list;
    }

    @Override
    protected ProgressBar getProgress() {
        return mBinding.progress;
    }

    protected ListViewAdapter newAdapter(List<ListItem> data) {
        return new ListViewAdapter(mActivity, new ArrayList<>(data));
    }

    @Override
    public void bindViews() {
        mBinding = ListViewWithSpinnerBinding.bind(mRootView);
    }
}
