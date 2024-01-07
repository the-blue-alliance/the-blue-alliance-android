package com.thebluealliance.androidclient.binders;

import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.viewbinding.ViewBinding;

import com.thebluealliance.androidclient.TbaLogger;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractListViewBinder<VB extends ViewBinding, T, AD extends ArrayAdapter<T>> extends AbstractDataBinder<List<T>, VB> {

    AD mAdapter;

    protected abstract ListView getList();
    protected abstract ProgressBar getProgress();

    protected abstract AD newAdapter(List<T> data);

    @Override
    public void updateData(@Nullable List<T> data) {
        if (data == null || mBinding == null) {
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
            getList().setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
        } else {
            replaceDataInAdapter(data);
        }

        getProgress().setVisibility(View.GONE);
        getList().setVisibility(View.VISIBLE);
        mNoDataBinder.unbindData();
        TbaLogger.d("BINDING COMPLETE; ELAPSED TIME: " + (System.currentTimeMillis() - startTime) + "ms");
        setDataBound(true);
    }

    protected void replaceDataInAdapter(List<T> data) {
        mAdapter.clear();
        mAdapter.addAll(new ArrayList<T>(data));
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onComplete() {
        if (mBinding != null) {
            getProgress().setVisibility(View.GONE);
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
            getList().setVisibility(View.GONE);
            getProgress().setVisibility(View.GONE);
            mNoDataBinder.bindData(mNoDataParams);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void unbind(boolean unbindViews) {
        super.unbind(unbindViews);
        if (mBinding != null) {
            getList().setVisibility(View.GONE);
            getProgress().setVisibility(View.VISIBLE);
        }
        if (unbindViews) {
            mBinding = null;
        }
    }

    @VisibleForTesting
    public ListView getListView() {
        return getList();
    }
}
