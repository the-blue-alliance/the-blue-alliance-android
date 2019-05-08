package com.thebluealliance.androidclient.binders;

import androidx.annotation.Nullable;
import android.view.View;

import com.thebluealliance.androidclient.TbaLogger;
import com.thebluealliance.androidclient.models.NoDataViewParams;
import com.thebluealliance.androidclient.views.NoDataView;

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
            TbaLogger.d("NoDataView not bound; NoDataViewParams cannot be null");
            return;
        }
        if (mNoDataView == null) {
            TbaLogger.d("NoDataView not bound; view cannot be null");
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
