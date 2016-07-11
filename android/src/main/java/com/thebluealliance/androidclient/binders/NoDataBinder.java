package com.thebluealliance.androidclient.binders;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.models.NoDataViewParams;
import com.thebluealliance.androidclient.views.NoDataView;

import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

/**
 * Display a "No Data Available" notice.
 * This doesn't <i>need</i> to extend {@link AbstractDataBinder}, but it may be a good idea later
 */
public class NoDataBinder {

    private NoDataView mNoDataView;

    public void setView(NoDataView view) {
        mNoDataView = view;
    }

    public void bindData(@Nullable NoDataViewParams data) {
        if (data == null) {
            Log.d(Constants.LOG_TAG, "NoDataView not bound; NoDataViewParams cannot be null");
            return;
        }
        if (mNoDataView == null) {
            Log.d(Constants.LOG_TAG, "NoDataView not bound; view cannot be null");
            return;
        }
        mNoDataView.setVisibility(View.VISIBLE);
        mNoDataView.setImage(data.getImageResId());
        mNoDataView.setText(data.getTextResId());
    }

    public void unbindData() {
        if (mNoDataView != null) {
            mNoDataView.setVisibility(View.GONE);
        }
    }
}
