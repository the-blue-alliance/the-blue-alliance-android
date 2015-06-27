package com.thebluealliance.androidclient.binders;

import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.adapters.ExpandableListAdapter;
import com.thebluealliance.androidclient.listitems.ListGroup;
import com.thebluealliance.androidclient.views.ExpandableListView;

import java.util.List;

public class ExpandableListBinder extends AbstractDataBinder<ExpandableListAdapter> {

    public static final short
      MODE_EXPAND_NONE = 0,
      MODE_EXPAND_FIRST = 1,
      MODE_EXPAND_ONLY = 2,
      MODE_EXPAND_ALL = 3;

    public ExpandableListView mExpandableList;
    public ProgressBar mProgressBar;

    private short mExpandMode;

    public ExpandableListBinder() {
        super();
        mExpandMode = MODE_EXPAND_NONE;
    }

    public void setExpandMode(short mode) {
        mExpandMode = mode;
    }

    @Override
    public void updateData(@Nullable ExpandableListAdapter data) {
        if (data == null || mExpandableList == null) {
            return;
        }

        if (mExpandableList.getAdapter() == null) {
            mExpandableList.setAdapter(data);
        }
        data.notifyDataSetChanged();
        mExpandableList.setVisibility(View.VISIBLE);
        expandForMode(data.groups);

        if (mProgressBar != null && !data.groups.isEmpty()) {
            mProgressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onComplete() {
        if (mProgressBar != null) {
            mProgressBar.setVisibility(View.GONE);
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
                    mExpandableList.expandGroup(i);
                }
                break;
            case MODE_EXPAND_FIRST:
                if (groups.size() > 0) {
                    mExpandableList.expandGroup(0);
                }
                for (int i = 1; i < groups.size(); i++) {
                    mExpandableList.collapseGroup(i);
                }
                break;
            case MODE_EXPAND_ONLY:
                if (groups.size() == 1) {
                    mExpandableList.expandGroup(0);
                } else {
                    for (int i = 0; i < groups.size(); i++) {
                        mExpandableList.collapseGroup(i);
                    }
                }
                break;
            case MODE_EXPAND_NONE:
                for (int i = 0; i < groups.size(); i++) {
                    mExpandableList.collapseGroup(i);
                }
                break;
        }
    }
}
