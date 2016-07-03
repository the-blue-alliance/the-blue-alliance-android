package com.thebluealliance.androidclient.datafeed;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.Single;
import rx.Subscription;
import rx.subjects.BehaviorSubject;

public class NetworkRequestStatusAggregator {

    private List<Single<Void>> mStatusObservables;
    private List<Subscription> mSubscriptions;
    private BehaviorSubject<Void> mSubject;
    private Observer<Void> mObserver;
    private boolean mStarted = false;
    private int mCompletedCount = 0;
    private int mErrorCount = 0;
    private List<Throwable> mErrors;

    public NetworkRequestStatusAggregator() {
        mStatusObservables = new ArrayList<>();
        mSubscriptions = new ArrayList<>();
        mErrors = new ArrayList<>();
        mSubject = BehaviorSubject.create();
        mObserver = new Observer<Void>() {
            @Override public void onCompleted() {
                mCompletedCount++;
                pushStatusUpdate();
            }

            @Override public void onError(Throwable e) {
                mErrorCount++;
                mErrors.add(e);
                pushStatusUpdate();
            }

            @Override public void onNext(Void aVoid) {
                // Do nothing
            }
        };
    }

    public void addRequestStatusObservable(Single<Void> observable) {
        mStatusObservables.add(observable);
    }

    public void addAllRequestStatusObservables(List<Single<Void>> observables) {
        for (Single<Void> observable : observables) {
            mStatusObservables.add(observable);
        }
    }

    public void start() {
        if (!mStarted) {
            for (Single<Void> single : mStatusObservables) {
                mSubscriptions.add(single.subscribe(mObserver));
            }
            mStarted = true;
        }
    }

    public Observable<Void> getObservable() {
        return mSubject.asObservable();
    }

    public void unsubscribeChildren() {
        for (Subscription sub : mSubscriptions) {
            sub.unsubscribe();
        }
    }

    private void pushStatusUpdate() {
        if (mCompletedCount + mErrorCount == mStatusObservables.size()) {
            if (mErrorCount > 0) {
                mSubject.onError(new Throwable("There were " + mErrorCount + " exceptions."));
            } else {
                mSubject.onCompleted();
            }
        }
    }
}
