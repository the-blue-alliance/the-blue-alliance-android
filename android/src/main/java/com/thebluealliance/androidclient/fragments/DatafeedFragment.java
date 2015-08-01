package com.thebluealliance.androidclient.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.thebluealliance.androidclient.binders.AbstractDataBinder;
import com.thebluealliance.androidclient.binders.NoDataBinder;
import com.thebluealliance.androidclient.datafeed.CacheableDatafeed;
import com.thebluealliance.androidclient.models.NoDataViewParams;
import com.thebluealliance.androidclient.modules.SubscriberModule;
import com.thebluealliance.androidclient.modules.components.FragmentComponent;
import com.thebluealliance.androidclient.modules.components.HasFragmentComponent;
import com.thebluealliance.androidclient.subscribers.BaseAPISubscriber;
import com.thebluealliance.androidclient.subscribers.EventBusSubscriber;

import javax.inject.Inject;

import dagger.Lazy;
import de.greenrobot.event.EventBus;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Easy abstraction of Fragment datafeed bindings
 * @param <T> Type returned by the API
 * @param <V> Type to be bound to a view
 * @param <S> {@link BaseAPISubscriber} that will take API Data -> prepare data to render
 * @param <B> {@link AbstractDataBinder} that will take prepared data -> view
 */
public abstract class DatafeedFragment
  <T, V, S extends BaseAPISubscriber<T, V>, B extends AbstractDataBinder<V>> extends Fragment {

    @Inject protected S mSubscriber;
    @Inject protected B mBinder;
    @Inject protected EventBus mEventBus;
    @Inject protected Lazy<EventBusSubscriber> mEventBusSubscriber;
    @Inject protected NoDataBinder mNoDataBinder;

    protected CacheableDatafeed mDatafeed;
    protected Observable<T> mObservable;
    protected FragmentComponent mComponent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActivity() instanceof HasFragmentComponent) {
            mComponent = ((HasFragmentComponent) getActivity()).getComponent();
        }
        mDatafeed = mComponent.datafeed();
        inject();
        mSubscriber.setConsumer(mBinder);
        mBinder.setActivity(getActivity());
        mBinder.setNoDataBinder(mNoDataBinder);
        mBinder.setNoDataParams(getNoDataParams());
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mSubscriber != null) {
            mObservable = getObservable();
            if (mObservable != null) {
                mObservable.subscribeOn(Schedulers.io())
                  .observeOn(Schedulers.computation())
                  .subscribe(mSubscriber);
            }
            Observable[] extras = getExtraObservables();
            if (shouldRegisterSubscriberToEventBus() || (extras != null && extras.length > 0)) {
                mEventBus.register(mSubscriber);
                for (int i = 0; extras != null && i < extras.length; i++) {
                    extras[i].subscribeOn(Schedulers.io())
                      .observeOn(Schedulers.computation())
                      .subscribe(mEventBusSubscriber.get());
                }
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mSubscriber != null) {
            mSubscriber.unsubscribe();
            mEventBus.unregister(mSubscriber);
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

    protected NoDataViewParams getNoDataParams() {
        return null;
    }

    /**
     * In case there are other endpoints that need to be hit
     */
    protected Observable[] getExtraObservables() {
        return null;
    }

    protected boolean shouldRegisterSubscriberToEventBus() {
        return false;
    }
}
