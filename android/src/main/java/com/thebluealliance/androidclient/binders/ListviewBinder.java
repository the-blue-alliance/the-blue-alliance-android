package com.thebluealliance.androidclient.binders;

import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.google.common.collect.ImmutableList;
import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.adapters.ListViewAdapter;
import com.thebluealliance.androidclient.listitems.ListItem;
import com.thebluealliance.androidclient.views.NoDataView;

import java.util.List;

public class ListViewBinder extends AbstractDataBinder<List<ListItem>> {

    public ListView mListView;
    public ProgressBar mProgressBar;

    @Override
    public void updateData(@Nullable List<ListItem> data) {
        if (data == null || mListView == null) {
            setDataBound(false);
            return;
        }
        if (data.isEmpty()) {
            setDataBound(false);
            return;
        }
        long startTime = System.currentTimeMillis();
        Log.d(Constants.LOG_TAG, "BINDING DATA");
        ListViewAdapter adapter = newAdapter(ImmutableList.copyOf(data));
        mListView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        if (mProgressBar != null) {
            mProgressBar.setVisibility(View.GONE);
        }

        mListView.setVisibility(View.VISIBLE);
        mNoDataBinder.unbindData();
        Log.d(Constants.LOG_TAG, "BINDING COMPLETE; ELAPSED TIME: " + (System.currentTimeMillis() - startTime) + "ms");
        setDataBound(true);
    }

    protected ListViewAdapter newAdapter(List<ListItem> data) {
        return new ListViewAdapter(mActivity, data);
    }

    @Override
    public void onComplete() {
        if (mProgressBar != null) {
            mProgressBar.setVisibility(View.GONE);
        }

        if (!isDataBound()) {
            bindNoDataView();
        }
    }

    @Override
    public void onError(Throwable throwable) {
        Log.e(Constants.LOG_TAG, Log.getStackTraceString(throwable));

        // If we received valid data from the cache but get an error from the network operations,
        // don't display the "No data" message.
        if (!isDataBound()) {
            bindNoDataView();
        }
    }

    private void bindNoDataView() {
        // Set up views for "no data" message
        try {
            mListView.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.GONE);
            mNoDataBinder.bindData(mNoDataParams);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
