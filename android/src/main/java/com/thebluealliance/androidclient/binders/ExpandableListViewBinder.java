package com.thebluealliance.androidclient.binders;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.adapters.ExpandableListViewAdapter;
import com.thebluealliance.androidclient.listitems.ListGroup;
import com.thebluealliance.androidclient.renderers.ModelRendererSupplier;
import com.thebluealliance.androidclient.views.ExpandableListView;

import android.support.annotation.Nullable;
import com.thebluealliance.androidclient.Log;
import android.view.View;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ExpandableListViewBinder extends AbstractDataBinder<List<ListGroup>> {

    public static final short
            MODE_EXPAND_NONE = 0,
            MODE_EXPAND_FIRST = 1,
            MODE_EXPAND_ONLY = 2,
            MODE_EXPAND_ALL = 3;

    @Bind(R.id.expandable_list) ExpandableListView expandableListView;
    @Bind(R.id.progress) ProgressBar progressBar;

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
        ButterKnife.bind(this, mRootView);
    }

    @Override
    public void updateData(@Nullable List<ListGroup> data) {
        if (data == null || expandableListView == null) {
            if (!isDataBound()) {
                setDataBound(false);
            }
            return;
        }
        if (data.isEmpty()) {
            Log.d("DATA IS EMPTY");
            if (!isDataBound()) {
                setDataBound(false);
            }
            return;
        }

        if (expandableListView.getExpandableListAdapter() == null) {
            expandableListView.setAdapter(newAdapter(new ArrayList<>(data)));
        } else {
            ExpandableListViewAdapter adapter = (ExpandableListViewAdapter) expandableListView.getExpandableListAdapter();
            adapter.removeAllGroups();
            adapter.addAllGroups(new ArrayList<>(data));
            adapter.notifyDataSetChanged();
        }

        expandableListView.setVisibility(View.VISIBLE);
        expandForMode(data);

        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }

        mNoDataBinder.unbindData();
        setDataBound(true);
    }

    protected ExpandableListViewAdapter newAdapter(List<ListGroup> data) {
        return new ExpandableListViewAdapter(mActivity, mRendererSupplier, data);
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
        Log.e(Log.getStackTraceString(throwable));

        // If we received valid data from the cache but get an error from the network operations,
        // don't display the "No data" message.
        if (!isDataBound()) {
            bindNoDataView();
        }
    }

    private void bindNoDataView() {
        // Set up views for "no data" message
        try {
            expandableListView.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
            mNoDataBinder.bindData(mNoDataParams);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void expandForMode(List<ListGroup> groups) {
        switch (mExpandMode) {
            case MODE_EXPAND_ALL:
                for (int i = 0; i < groups.size(); i++) {
                    expandableListView.expandGroup(i);
                }
                break;
            case MODE_EXPAND_FIRST:
                if (groups.size() > 0) {
                    expandableListView.expandGroup(0);
                }
                for (int i = 1; i < groups.size(); i++) {
                    expandableListView.collapseGroup(i);
                }
                break;
            case MODE_EXPAND_ONLY:
                if (groups.size() == 1) {
                    expandableListView.expandGroup(0);
                } else {
                    for (int i = 0; i < groups.size(); i++) {
                        expandableListView.collapseGroup(i);
                    }
                }
                break;
            case MODE_EXPAND_NONE:
                for (int i = 0; i < groups.size(); i++) {
                    expandableListView.collapseGroup(i);
                }
                break;
        }
    }

    @Override
    public void unbind(boolean unbindViews) {
        super.unbind(unbindViews);
        if (unbindViews) {
            ButterKnife.unbind(this);
        }
        if (expandableListView != null) {
            expandableListView.setVisibility(View.GONE);
        }
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
    }
}
