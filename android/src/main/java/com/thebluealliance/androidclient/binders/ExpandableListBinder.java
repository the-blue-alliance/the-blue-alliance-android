package com.thebluealliance.androidclient.binders;

import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.google.common.collect.ImmutableList;
import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.adapters.ExpandableListAdapter;
import com.thebluealliance.androidclient.listitems.ListGroup;
import com.thebluealliance.androidclient.views.ExpandableListView;

import java.util.List;

public class ExpandableListBinder extends AbstractDataBinder<List<ListGroup>> {

    public static final short
      MODE_EXPAND_NONE = 0,
      MODE_EXPAND_FIRST = 1,
      MODE_EXPAND_ONLY = 2,
      MODE_EXPAND_ALL = 3;

    public ExpandableListView expandableList;
    public ProgressBar progressBar;

    private short mExpandMode;

    public ExpandableListBinder() {
        super();
        mExpandMode = MODE_EXPAND_NONE;
    }

    public void setExpandMode(short mode) {
        mExpandMode = mode;
    }

    @Override
    public void updateData(@Nullable List<ListGroup> data) {
        if (data == null || expandableList == null) {
            return;
        }

        ExpandableListAdapter adapter = newAdapter(ImmutableList.copyOf(data));
        expandableList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        expandableList.setVisibility(View.VISIBLE);
        expandForMode(data);

        if (progressBar != null && !data.isEmpty()) {
            progressBar.setVisibility(View.GONE);
        }
    }

    protected ExpandableListAdapter newAdapter(List<ListGroup> data) {
        return new ExpandableListAdapter(mActivity, data);
    }

    @Override
    public void onComplete() {
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }

        // TODO no data text
    }

    @Override
    public void onError(Throwable throwable) {
        Log.e(Constants.LOG_TAG, Log.getStackTraceString(throwable));
    }

    private void expandForMode(List<ListGroup> groups) {
        switch (mExpandMode) {
            case MODE_EXPAND_ALL:
                for (int i = 0; i < groups.size(); i++) {
                    expandableList.expandGroup(i);
                }
                break;
            case MODE_EXPAND_FIRST:
                if (groups.size() > 0) {
                    expandableList.expandGroup(0);
                }
                for (int i = 1; i < groups.size(); i++) {
                    expandableList.collapseGroup(i);
                }
                break;
            case MODE_EXPAND_ONLY:
                if (groups.size() == 1) {
                    expandableList.expandGroup(0);
                } else {
                    for (int i = 0; i < groups.size(); i++) {
                        expandableList.collapseGroup(i);
                    }
                }
                break;
            case MODE_EXPAND_NONE:
                for (int i = 0; i < groups.size(); i++) {
                    expandableList.collapseGroup(i);
                }
                break;
        }
    }
}
