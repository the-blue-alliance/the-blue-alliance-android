package com.thebluealliance.androidclient.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.thebluealliance.androidclient.binders.AbstractDataBinder;
import com.thebluealliance.androidclient.binders.NoDataBinder;
import com.thebluealliance.androidclient.datafeed.CacheableDatafeed;
import com.thebluealliance.androidclient.datafeed.refresh.RefreshController;
import com.thebluealliance.androidclient.datafeed.refresh.RefreshController.RefreshType;
import com.thebluealliance.androidclient.datafeed.refresh.Refreshable;
import com.thebluealliance.androidclient.datafeed.retrofit.APIv2;
import com.thebluealliance.androidclient.models.NoDataViewParams;
import com.thebluealliance.androidclient.subscribers.SubscriberModule;
import com.thebluealliance.androidclient.di.components.FragmentComponent;
import com.thebluealliance.androidclient.di.components.HasFragmentComponent;
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
  <T, V, S extends BaseAPISubscriber<T, V>, B extends AbstractDataBinder<V>>
  extends Fragment implements Refreshable {

    @Inject protected S mSubscriber;
    @Inject protected B mBinder;
    @Inject protected EventBus mEventBus;
    @Inject protected Lazy<EventBusSubscriber> mEventBusSubscriber;
    @Inject protected NoDataBinder mNoDataBinder;
    @Inject protected RefreshController mRefreshController;

    protected CacheableDatafeed mDatafeed;
    protected Observable<? extends T> mObservable;
    protected FragmentComponent mComponent;
    protected String mRefreshTag;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActivity() instanceof HasFragmentComponent) {
            mComponent = ((HasFragmentComponent) getActivity()).getComponent();
        }
        inject();
        mRefreshTag = getRefreshTag();
        mDatafeed = mComponent.datafeed();
        mSubscriber.setConsumer(mBinder);
        mSubscriber.setRefreshController(mRefreshController);
        mSubscriber.setRefreshTag(mRefreshTag);
        mBinder.setActivity(getActivity());
        mBinder.setNoDataBinder(mNoDataBinder);
        mBinder.setNoDataParams(getNoDataParams());
    }

    @Override
    public void onResume() {
        super.onResume();
        getNewObservables(RefreshController.NOT_REQUESTED_BY_USER);
        mRefreshController.registerRefreshable(mRefreshTag, this);
        if (shouldRegisterSubscriberToEventBus()) {
            mEventBus.register(mSubscriber);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mRefreshController.unregisterRefreshable(mRefreshTag);
        if (mSubscriber != null) {
            if (shouldRegisterSubscriberToEventBus()) {
                mEventBus.unregister(mSubscriber);
            }
        }
    }

    /**
     * Allows other things to bind this instance
     */
    public void bind() {
        if (mSubscriber != null) {
            mSubscriber.bindData();
        }
    }

    public void setShouldBindImmediately(boolean shouldBind) {
        if (mSubscriber != null) {
            mSubscriber.setShouldBindImmediately(shouldBind);
        }
    }

    /**
     * Registers and subscribes new observables
     */
    private void getNewObservables(@RefreshType int refreshType) {
        if (mSubscriber != null) {
            mObservable = getObservable(
              refreshType == RefreshController.REQUESTED_BY_USER
                ? APIv2.TBA_CACHE_WEB
                : null);
            if (mObservable != null) {
                mObservable.subscribeOn(Schedulers.io())
                        .observeOn(Schedulers.computation())
                        .subscribe(mSubscriber);
            }

        }
    }

    @Override
    public void onRefreshStart(@RefreshType int refreshType) {
        if (mSubscriber != null && mBinder != null) {
            mBinder.unbind();
            getNewObservables(RefreshController.REQUESTED_BY_USER);
            mRefreshController.notifyRefreshingStateChanged(mRefreshTag, true);
            mSubscriber.onRefreshStart();
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
     * @param tbaCacheHeader String param to tell the datafeed how to load the data. Use
     * {@link APIv2#TBA_CACHE_WEB}, {@link APIv2#TBA_CACHE_LOCAL}, or {@code null} for regular usage
     */
    protected abstract Observable<? extends T> getObservable(String tbaCacheHeader);

    /**
     * @return A string identifying what data this fragment is loading
     */
    protected abstract String getRefreshTag();

    protected NoDataViewParams getNoDataParams() {
        return null;
    }

    protected boolean shouldRegisterSubscriberToEventBus() {
        return false;
    }
}
