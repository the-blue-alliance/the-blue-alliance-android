package com.thebluealliance.androidclient.datafeed;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.Single;
import rx.Subscription;
import rx.observables.ConnectableObservable;
import rx.subjects.BehaviorSubject;

public class ObservableCompletionAggregator {

    private List<ConnectableObservable<?>> mObservables;
    private List<Subscription> mSubscriptions;
    private BehaviorSubject<Void> mSubject;
    private Observer<Object> mObserver;
    private boolean mStarted = false;
    private int mCompletedCount = 0;
    private int mErrorCount = 0;
    private List<Throwable> mErrors;

    public ObservableCompletionAggregator() {
        mObservables = new ArrayList<>();
        mSubscriptions = new ArrayList<>();
        mErrors = new ArrayList<>();
        mSubject = BehaviorSubject.create();
        mObserver = new Observer<Object>() {
            @Override public void onCompleted() {
                mCompletedCount++;
                pushStatusUpdate();
            }

            @Override public void onError(Throwable e) {
                mErrorCount++;
                mErrors.add(e);
                pushStatusUpdate();
            }

            @Override public void onNext(Object object) {
                // Do nothing
            }
        };
    }

    public void addObservable(Observable<?> observable) {
        if (mStarted) {
            return;
        }

        ConnectableObservable<?> o = observable.publish();
        mObservables.add(o);
        mSubscriptions.add(o.subscribe(mObserver));
    }

    public void addSingle(Single<?> single) {
        if (mStarted) {
            return;
        }

        ConnectableObservable<?> observable = single.toObservable().publish();
        mObservables.add(observable);
        mSubscriptions.add(observable.subscribe(mObserver));
    }

    public void addAllObservables(List<Observable<?>> observables) {
        if (mStarted) {
            return;
        }

        for (Observable observable : observables) {
            ConnectableObservable<?> o = observable.publish();
            mObservables.add(o);
            mSubscriptions.add(o.subscribe(mObserver));
        }
    }

    public <T> void addAllSingles(List<Single<?>> singles) {
        if (mStarted) {
            return;
        }

        for (Single<?> single : singles) {
            ConnectableObservable<?> observable = single.toObservable().publish();
            mObservables.add(observable);
            mSubscriptions.add(observable.subscribe(mObserver));
        }
    }

    public Observable<Void> getObservable() {
        return mSubject.asObservable();
    }

    public void start() {
        if (!mStarted) {
            for (ConnectableObservable<?> observable : mObservables) {
                observable.connect();
            }
            mStarted = true;
        }
    }

    public void unsubscribeChildren() {
        for (Subscription sub : mSubscriptions) {
            sub.unsubscribe();
        }
    }

    private void pushStatusUpdate() {
        if (mCompletedCount + mErrorCount >= mSubscriptions.size()) {
            if (mErrorCount > 0) {
                mSubject.onError(new Throwable("There were " + mErrorCount + " exceptions."));
            } else {
                mSubject.onNext(null);
                mSubject.onCompleted();
            }
        }
    }
}
