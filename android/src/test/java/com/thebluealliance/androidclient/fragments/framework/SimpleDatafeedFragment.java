package com.thebluealliance.androidclient.fragments.framework;

import androidx.annotation.VisibleForTesting;

import com.google.android.gms.analytics.Tracker;
import com.thebluealliance.androidclient.binders.NoDataBinder;
import com.thebluealliance.androidclient.fragments.DatafeedFragment;

import dagger.hilt.android.AndroidEntryPoint;
import rx.Observable;

/**
 * A very basic {@link DatafeedFragment} to test framework bindings
 */
@AndroidEntryPoint
public class SimpleDatafeedFragment extends DatafeedFragment<String, String, SimpleSubscriber, SimpleBinder> {

    private Observable<? extends String> mTestObservable;

    @Override
    protected Observable<? extends String> getObservable(String tbaCacheHeader) {
        return mTestObservable;
    }

    @Override
    protected String getRefreshTag() {
        return "SimpleTest";
    }

    @VisibleForTesting
    public void setObservable(Observable<? extends String> observable) {
        mTestObservable = observable;
    }

    @VisibleForTesting
    public SimpleSubscriber getSubscriber() {
        return mSubscriber;
    }

    @VisibleForTesting
    public SimpleBinder getBinder() {
        return mBinder;
    }

    @VisibleForTesting
    public NoDataBinder getNoDataBinder() {
        return mNoDataBinder;
    }

    @VisibleForTesting
    public Tracker getAnalyticsTracker() {
        return mAnalyticsTracker;
    }
}
