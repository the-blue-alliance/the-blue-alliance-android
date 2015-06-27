package com.thebluealliance.androidclient.binders;

import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.adapters.ListViewAdapter;

public class ListviewBinder extends AbstractDataBinder<ListViewAdapter> {

    public ListView mListView;
    public ProgressBar mProgressBar;

    @Override
    public void updateData(@Nullable ListViewAdapter data) {
        if (data == null || mListView == null) {
            return;
        }
        if (mListView.getAdapter() == null) {
            mListView.setAdapter(data);
        }
        data.notifyDataSetChanged();

        if (mProgressBar != null && !data.values.isEmpty()) {
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
}
