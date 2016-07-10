package com.thebluealliance.androidclient.subscribers;

import com.thebluealliance.androidclient.datafeed.ObservableCompletionAggregator;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;

public abstract class BriteBaseAPISubscriber<APIType, BindType> extends BaseAPISubscriber<APIType, BindType> {

    protected Observer<Void> mNetworkStatusObserver;

    public BriteBaseAPISubscriber() {
        super();
        mNetworkStatusObserver = new Observer<Void>() {
            @Override public void onCompleted() {
                mRefreshController.notifyRefreshingStateChanged(mRefreshTag, false);
            }

            @Override public void onError(Throwable e) {
                mRefreshController.notifyRefreshingStateChanged(mRefreshTag, false);
            }

            @Override public void onNext(Void aVoid) {
                // Do nothing
            }
        };
    }

    public void subscribeNetworkRequestStatusAggregator(ObservableCompletionAggregator agg) {
        agg.getObservable().observeOn(AndroidSchedulers.mainThread()).subscribe(mNetworkStatusObserver);
        agg.start();
    }

}
