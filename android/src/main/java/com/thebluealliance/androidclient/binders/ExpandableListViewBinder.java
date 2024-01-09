package com.thebluealliance.androidclient.binders;

import android.view.View;

import androidx.annotation.Nullable;

import com.thebluealliance.androidclient.TbaLogger;
import com.thebluealliance.androidclient.adapters.ExpandableListViewAdapter;
import com.thebluealliance.androidclient.databinding.ExpandableListViewWithSpinnerBinding;
import com.thebluealliance.androidclient.listitems.ListGroup;
import com.thebluealliance.androidclient.renderers.ModelRendererSupplier;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class ExpandableListViewBinder extends AbstractDataBinder<List<ListGroup>, ExpandableListViewWithSpinnerBinding> {

    public static final short
            MODE_EXPAND_NONE = 0,
            MODE_EXPAND_FIRST = 1,
            MODE_EXPAND_ONLY = 2,
            MODE_EXPAND_ALL = 3;

    private short mExpandMode;
    protected ModelRendererSupplier mRendererSupplier;

    @Inject
    public ExpandableListViewBinder(ModelRendererSupplier supplier) {
        super();
        mRendererSupplier = supplier;
        mExpandMode = MODE_EXPAND_NONE;
    }

    public void setExpandMode(short mode) {
        mExpandMode = mode;
    }

    @Override
    public void bindViews() {
        mBinding = ExpandableListViewWithSpinnerBinding.bind(mRootView);
    }

    @Override
    public void updateData(@Nullable List<ListGroup> data) {
        if (data == null || mBinding == null) {
            if (!isDataBound()) {
                setDataBound(false);
            }
            return;
        }
        if (data.isEmpty()) {
            TbaLogger.d("DATA IS EMPTY");
            if (!isDataBound()) {
                setDataBound(false);
            }
            return;
        }

        if (mBinding.expandableList.getExpandableListAdapter() == null) {
            mBinding.expandableList.setAdapter(newAdapter(new ArrayList<>(data)));
        } else {
            ExpandableListViewAdapter adapter = (ExpandableListViewAdapter) mBinding.expandableList.getExpandableListAdapter();
            adapter.removeAllGroups();
            adapter.addAllGroups(new ArrayList<>(data));
            adapter.notifyDataSetChanged();
        }

        mBinding.expandableList.setVisibility(View.VISIBLE);
        expandForMode(data);

        mBinding.progress.setVisibility(View.GONE);
        mNoDataBinder.unbindData();
        setDataBound(true);
    }

    protected ExpandableListViewAdapter newAdapter(List<ListGroup> data) {
        return new ExpandableListViewAdapter(mActivity, mRendererSupplier, data);
    }

    @Override
    public void onComplete() {
        if (mBinding != null) {
            mBinding.progress.setVisibility(View.GONE);
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
            mBinding.expandableList.setVisibility(View.GONE);
            mBinding.progress.setVisibility(View.GONE);
            mNoDataBinder.bindData(mNoDataParams);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void expandForMode(List<ListGroup> groups) {
        switch (mExpandMode) {
            case MODE_EXPAND_ALL:
                for (int i = 0; i < groups.size(); i++) {
                    mBinding.expandableList.expandGroup(i);
                }
                break;
            case MODE_EXPAND_FIRST:
                if (groups.size() > 0) {
                    mBinding.expandableList.expandGroup(0);
                }
                for (int i = 1; i < groups.size(); i++) {
                    mBinding.expandableList.collapseGroup(i);
                }
                break;
            case MODE_EXPAND_ONLY:
                if (groups.size() == 1) {
                    mBinding.expandableList.expandGroup(0);
                } else {
                    for (int i = 0; i < groups.size(); i++) {
                        mBinding.expandableList.collapseGroup(i);
                    }
                }
                break;
            case MODE_EXPAND_NONE:
                for (int i = 0; i < groups.size(); i++) {
                    mBinding.expandableList.collapseGroup(i);
                }
                break;
        }
    }

    @Override
    public void unbind(boolean unbindViews) {
        super.unbind(unbindViews);
        if (mBinding != null) {
            mBinding.expandableList.setVisibility(View.GONE);
            mBinding.progress.setVisibility(View.VISIBLE);
        }
        if (unbindViews) {
            mBinding = null;
        }
    }
}
