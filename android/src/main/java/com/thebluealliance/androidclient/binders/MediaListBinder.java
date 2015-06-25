package com.thebluealliance.androidclient.binders;

import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.adapters.ExpandableListAdapter;
import com.thebluealliance.androidclient.models.BasicModel;
import com.thebluealliance.androidclient.views.ExpandableListView;

public class MediaListBinder extends AbstractDataBinder<ExpandableListAdapter> {

    public ExpandableListView mExpandableList;
    public ProgressBar mProgressBar;

    @Override
    public void updateData(@Nullable ExpandableListAdapter data)
      throws BasicModel.FieldNotDefinedException {
        if (data == null || mExpandableList == null) {
            return;
        }

        if (mExpandableList.getAdapter() == null) {
            mExpandableList.setAdapter(data);
        }
        mExpandableList.setVisibility(View.VISIBLE);
        data.notifyDataSetChanged();

        for (int i = 0; i < data.groups.size(); i++) {
            mExpandableList.expandGroup(i);
        }

        if (mProgressBar != null) {
            mProgressBar.setVisibility(View.GONE);
        }

        // TODO no data text
    }

    @Override
    public void onError(Throwable throwable) {
        Log.e(Constants.LOG_TAG, Log.getStackTraceString(throwable));
    }
}
