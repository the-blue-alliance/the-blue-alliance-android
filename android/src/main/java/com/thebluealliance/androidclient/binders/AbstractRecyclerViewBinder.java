package com.thebluealliance.androidclient.binders;

import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;

import com.thebluealliance.androidclient.TbaLogger;

import java.util.ArrayList;
import java.util.List;

import io.nlopez.smartadapters.SmartAdapter;
import io.nlopez.smartadapters.adapters.RecyclerMultiAdapter;

public abstract class AbstractRecyclerViewBinder<VB extends ViewBinding> extends AbstractDataBinder<List<Object>, VB> {
    protected RecyclerViewAdapterCreatorInitializer mMapper;
    protected RecyclerMultiAdapter mAdapter;

    protected List<Object> mList;

    protected abstract RecyclerView getList();
    protected abstract ProgressBar getProgress();

    public void setRecyclerViewBinderMapper(RecyclerViewAdapterCreatorInitializer mapper) {
        mMapper = mapper;
    }

    @Override
    public void updateData(@Nullable List<Object> data) {
        mList = data;
        if (data == null || mBinding == null) {
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

        getProgress().setVisibility(View.GONE);
        getList().setVisibility(View.VISIBLE);
        mNoDataBinder.unbindData();
        setDataBound(true);
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

    private void bindNoDataView() {
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

    protected void createAndInitializeAdapterForData(List<?> data) {
        SmartAdapter.MultiAdaptersCreator creator = SmartAdapter.items(new ArrayList<>(data));
        mMapper.initializeAdapterCreator(creator);
        mAdapter = creator.into(getList());
    }

    public interface RecyclerViewAdapterCreatorInitializer {
        void initializeAdapterCreator(SmartAdapter.MultiAdaptersCreator creator);
    }
}
