package com.thebluealliance.androidclient.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.thebluealliance.androidclient.binders.AbstractDataBinder;
import com.thebluealliance.androidclient.datafeed.CacheableDatafeed;
import com.thebluealliance.androidclient.modules.SubscriberModule;
import com.thebluealliance.androidclient.modules.components.FragmentComponent;
import com.thebluealliance.androidclient.modules.components.HasFragmentComponent;
import com.thebluealliance.androidclient.subscribers.BaseAPISubscriber;

import javax.inject.Inject;

import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Easy abstraction of Fragment datafeed bindings
 * @param <T> Type returned by the API
 * @param <V> Type to be bound to a view
 * @param <S> {@link BaseAPISubscriber} that will take API Data -> prepare data to render
 * @param <B> {@link AbstractDataBinder} that will take prepared data -> view
 */
public abstract class DatafeedFragment<
  T, V, S extends BaseAPISubscriber<T, V>, B extends AbstractDataBinder<V>> extends Fragment {

    @Inject protected S mSubscriber;
    @Inject protected B mBinder;
    @Inject protected CacheableDatafeed mDatafeed;

    protected Observable<T> mObservable;
    protected FragmentComponent mComponent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActivity() instanceof HasFragmentComponent) {
            mComponent = ((HasFragmentComponent) getActivity()).getComponent();
        }
        inject();
        mSubscriber.setConsumer(mBinder);
        mBinder.setContext(getActivity());
    }

    @Override
    public void onResume() {
        super.onResume();
        mObservable = getObservable();
        mObservable.subscribeOn(Schedulers.io())
          .observeOn(Schedulers.computation())
          .subscribe(mSubscriber);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mSubscriber != null) {
            mSubscriber.unsubscribe();
        }
    }

    /**
     * Fragments should inject themselves (to preserve Dagger's strong typing)
     * They just need to have the following line: mComponent.inject(this);
     * Plus, whatever else they want
     * If the types don't match, add the fragment to {@link SubscriberModule} and rebuild
     * Called in {@link #onCreate(Bundle)}
     */
    protected abstract void inject();

    /**
     * For child to make a call to return the Observable containing the main data model
     * Called in {@link #onResume()}
     */
    protected abstract Observable<T> getObservable();
}
