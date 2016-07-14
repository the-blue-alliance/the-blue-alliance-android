package com.thebluealliance.androidclient.binders;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.R;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import com.thebluealliance.androidclient.Log;
import android.view.View;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.nlopez.smartadapters.SmartAdapter;
import io.nlopez.smartadapters.adapters.RecyclerMultiAdapter;

public class RecyclerViewBinder extends AbstractDataBinder<List<Object>> {

    @Bind(R.id.list) RecyclerView mRecyclerView;
    @Bind(R.id.progress) ProgressBar mProgressBar;

    protected RecyclerViewAdapterCreatorInitializer mMapper;
    protected RecyclerMultiAdapter mAdapter;

    protected List<Object> mList;

    public void setRecyclerViewBinderMapper(RecyclerViewAdapterCreatorInitializer mapper) {
        mMapper = mapper;
    }

    @Override
    public void bindViews() {
        ButterKnife.bind(this, mRootView);
    }

    @Override
    public void updateData(@Nullable List<Object> data) {
        mList = data;
        if (data == null || mRecyclerView == null) {
            setDataBound(false);
            return;
        }
        if (data.isEmpty()) {
            setDataBound(false);
            return;
        }
        if (mMapper == null) {
            throw new RuntimeException(this.getClass().getName() + " does not have a RecyclerViewAdapterCreatorInitializer set!");
        }

        if (mAdapter == null) {
            createAndInitializeAdapterForData(new ArrayList<>(data));
        } else {
            mAdapter.clearItems();
            mAdapter.addItems(new ArrayList<>(data));
        }

        if (mProgressBar != null) {
            mProgressBar.setVisibility(View.GONE);
        }

        mRecyclerView.setVisibility(View.VISIBLE);
        mNoDataBinder.unbindData();
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

    protected void createAndInitializeAdapterForData(List<Object> data) {
        SmartAdapter.MultiAdaptersCreator creator = SmartAdapter.items(new ArrayList<>(data));
        mMapper.initializeAdapterCreator(creator);
        mAdapter = creator.into(mRecyclerView);
    }

    public interface RecyclerViewAdapterCreatorInitializer {
        void initializeAdapterCreator(SmartAdapter.MultiAdaptersCreator creator);
    }
}
