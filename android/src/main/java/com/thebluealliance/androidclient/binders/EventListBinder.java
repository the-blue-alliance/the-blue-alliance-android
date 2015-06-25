package com.thebluealliance.androidclient.binders;

import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.adapters.ListViewAdapter;
import com.thebluealliance.androidclient.models.BasicModel;

public class EventListBinder extends AbstractDataBinder<ListViewAdapter> {

    public ListView mListView;
    public ProgressBar mProgressBar;

    @Override
    public void updateData(@Nullable ListViewAdapter data)
      throws BasicModel.FieldNotDefinedException {
        if (data == null || mListView == null) {
            return;
        }
        if (mListView.getAdapter() == null) {
            mListView.setAdapter(data);
        }
        data.notifyDataSetChanged();

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
