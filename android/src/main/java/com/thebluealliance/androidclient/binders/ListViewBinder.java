package com.thebluealliance.androidclient.binders;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.adapters.ListViewAdapter;
import com.thebluealliance.androidclient.listitems.ListItem;

import android.support.annotation.Nullable;
import com.thebluealliance.androidclient.TbaLogger;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ListViewBinder extends AbstractDataBinder<List<ListItem>> {

    @Bind(R.id.list) ListView listView;
    @Bind(R.id.progress) ProgressBar progressBar;

    ListViewAdapter mAdapter;

    @Override
    public void bindViews() {
        ButterKnife.bind(this, mRootView);
    }

    @Override
    public void updateData(@Nullable List<ListItem> data) {
        if (data == null || listView == null) {
            if (!isDataBound()) {
                setDataBound(false);
            }
            return;
        }
        if (data.isEmpty()) {
            if (!isDataBound()) {
                setDataBound(false);
            }
            return;
        }
        long startTime = System.currentTimeMillis();
        TbaLogger.d("BINDING DATA");
        if (mAdapter == null) {
            mAdapter = newAdapter(data);
            listView.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
        } else {
            replaceDataInAdapter(data);
        }

        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }

        listView.setVisibility(View.VISIBLE);
        mNoDataBinder.unbindData();
        TbaLogger.d("BINDING COMPLETE; ELAPSED TIME: " + (System.currentTimeMillis() - startTime) + "ms");
        setDataBound(true);
    }

    protected ListViewAdapter newAdapter(List<ListItem> data) {
        return new ListViewAdapter(mActivity, new ArrayList<>(data));
    }

    protected void replaceDataInAdapter(List<ListItem> data) {
        mAdapter.clear();
        mAdapter.addAll(new ArrayList<>(data));
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onComplete() {
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }

        if (!isDataBound()) {
            bindNoDataView();
        }
    }

    @Override
    public void onError(Throwable throwable) {
        TbaLogger.e(TbaLogger.getStackTraceString(throwable));

        // If we received valid data from the cache but get an error from the network operations,
        // don't display the "No data" message.
        if (!isDataBound()) {
            bindNoDataView();
        }
    }

    protected void bindNoDataView() {
        // Set up views for "no data" message
        try {
            listView.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
            mNoDataBinder.bindData(mNoDataParams);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void unbind(boolean unbindViews) {
        super.unbind(unbindViews);
        if (unbindViews) {
            ButterKnife.unbind(this);
        }
        if (listView != null) {
            listView.setVisibility(View.GONE);
        }
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
    }
}
