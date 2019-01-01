package com.thebluealliance.androidclient.fragments.framework;

import android.support.annotation.VisibleForTesting;

import com.google.android.gms.analytics.Tracker;
import com.thebluealliance.androidclient.binders.NoDataBinder;
import com.thebluealliance.androidclient.di.MockFragmentComponent;
import com.thebluealliance.androidclient.fragments.DatafeedFragment;

import rx.Observable;

/**
 * A very basic {@link DatafeedFragment} to test framework bindings
 */
public class SimpleDatafeedFragment extends DatafeedFragment<String, String, SimpleSubscriber, SimpleBinder> {

    private Observable<? extends String> mTestObservable;

    @Override
    protected void inject() {
        ((MockFragmentComponent) mComponent).inject(this);
    }

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
