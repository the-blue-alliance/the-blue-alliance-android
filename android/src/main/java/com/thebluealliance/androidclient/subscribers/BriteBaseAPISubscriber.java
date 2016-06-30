package com.thebluealliance.androidclient.subscribers;

import com.thebluealliance.androidclient.datafeed.NetworkRequestStatusAggregator;

import rx.Observer;

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

    public void subscribeAndStartNetworkRequestStatusAggregator(NetworkRequestStatusAggregator agg) {
        agg.getObservable().subscribe(mNetworkStatusObserver);
        agg.start();
    }

}
