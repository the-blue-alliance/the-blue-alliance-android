package com.thebluealliance.androidclient.binders;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.datafeed.DataConsumer;
import com.thebluealliance.androidclient.models.NoDataViewParams;
import com.thebluealliance.androidclient.views.NoDataView;

import android.app.Activity;
import android.util.Log;
import android.view.View;

/**
 * A class that takes in input data model and updates views accordingly
 * For now, declare views as public members and set them elsewhere where the view is created
 * (like
 * {@link android.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)}).
 *
 * @param <T>
 */
public abstract class AbstractDataBinder<T> implements DataConsumer<T> {
    Activity mActivity;
    protected NoDataBinder mNoDataBinder;
    protected NoDataViewParams mNoDataParams;
    View mRootView;

    private boolean mIsDataBound;

    public AbstractDataBinder() {
        mIsDataBound = false;
    }

    public void setActivity(Activity activity) {
        mActivity = activity;
    }

    /**
     * Call this in
     * {@link android.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)}).
     */
    public void setRootView(View view){
        mRootView = view;
    }

    public void setNoDataBinder(NoDataBinder binder) {
        mNoDataBinder = binder;
    }

    public void setNoDataParams(NoDataViewParams params) {
        mNoDataParams = params;
    }

    public boolean isDataBound() {
        return mIsDataBound;
    }

    public void setDataBound(boolean isDataBound) {
        mIsDataBound = isDataBound;
    }

    public void setNoDataView(NoDataView noDataView) {
        mNoDataBinder.setView(noDataView);
    }

    public void unbind(boolean unbindViews) {
        Log.d(Constants.LOG_TAG, "UNBINDING");
        setDataBound(false);
        /* Child classes can unbind their own views. */
        /** Don't show NoDataBinder because we'll call this from {@link Fragment#onDestroyView()} */
    }
}
