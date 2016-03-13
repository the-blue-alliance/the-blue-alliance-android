package com.thebluealliance.androidclient.binders;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.adapters.ListViewAdapter;
import com.thebluealliance.androidclient.listitems.ListItem;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.nlopez.smartadapters.SmartAdapter;
import io.nlopez.smartadapters.adapters.RecyclerMultiAdapter;

public class RecyclerViewBinder extends AbstractDataBinder<List<Object>> {

    @Bind(R.id.list) RecyclerView mRecyclerView;
    @Bind(R.id.progress) ProgressBar mProgressBar;

    protected RecyclerViewBinderMapper mMapper;
    protected RecyclerMultiAdapter mAdapter;

    protected List<Object> mOldList;

    public void setRecyclerViewBinderMapper(RecyclerViewBinderMapper mapper) {
        mMapper = mapper;
    }

    @Override
    public void bindViews() {
        ButterKnife.bind(this, mRootView);
    }

    @Override
    public void updateData(@Nullable List<Object> data) {
        if (data == null || mRecyclerView == null) {
            setDataBound(false);
            return;
        }
        if (data.isEmpty()) {
            setDataBound(false);
            return;
        }
        if (mMapper == null) {
            throw new RuntimeException(this.getClass().getName() + " does not have a RecyclerViewBinderMapper set!");
        }

        long startTime = System.currentTimeMillis();
        Log.d(Constants.LOG_TAG, "BINDING DATA");
        if (mAdapter == null) {
            SmartAdapter.MultiAdaptersCreator creator = SmartAdapter.items(new ArrayList<>(data));
            mMapper.initializeMaps(creator);
            mAdapter = creator.into(mRecyclerView);
        } else if (mOldList == null) {
            mAdapter.clearItems();
            mAdapter.addItems(new ArrayList<>(data));
        } else {
            // Compute which items are new
        }

        if (mProgressBar != null) {
            mProgressBar.setVisibility(View.GONE);
        }

        mRecyclerView.setVisibility(View.VISIBLE);
        mNoDataBinder.unbindData();
        Log.d(Constants.LOG_TAG, "BINDING COMPLETE; ELAPSED TIME: " + (System.currentTimeMillis() - startTime) + "ms");
        setDataBound(true);
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
            mRecyclerView.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.GONE);
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
        if (mRecyclerView != null) {
            mRecyclerView.setVisibility(View.GONE);
        }
        if (mProgressBar != null) {
            mProgressBar.setVisibility(View.VISIBLE);
        }
    }

    public interface RecyclerViewBinderMapper {
        void initializeMaps(SmartAdapter.MultiAdaptersCreator creator);
    }
}
