package com.thebluealliance.androidclient.binders;

import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.google.common.collect.ImmutableList;
import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.adapters.ListViewAdapter;
import com.thebluealliance.androidclient.listitems.ListItem;

import java.util.List;

public class ListviewBinder extends AbstractDataBinder<List<ListItem>> {

    public ListView listView;
    public ProgressBar progressBar;

    @Override
    public void updateData(@Nullable List<ListItem> data) {
        if (data == null || listView == null) {
            return;
        }
        ListViewAdapter adapter = newAdapter(ImmutableList.copyOf(data));
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        if (progressBar != null && !data.isEmpty()) {
            progressBar.setVisibility(View.GONE);
        }
    }

    protected ListViewAdapter newAdapter(List<ListItem> data) {
        return new ListViewAdapter(mActivity, data);
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
}
