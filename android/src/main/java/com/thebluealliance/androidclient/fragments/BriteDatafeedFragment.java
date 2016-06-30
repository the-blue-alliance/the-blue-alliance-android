package com.thebluealliance.androidclient.fragments;

import com.google.android.gms.analytics.Tracker;

import com.thebluealliance.androidclient.binders.AbstractDataBinder;
import com.thebluealliance.androidclient.binders.NoDataBinder;
import com.thebluealliance.androidclient.datafeed.BriteDatafeed;
import com.thebluealliance.androidclient.datafeed.NetworkRequestStatusAggregator;
import com.thebluealliance.androidclient.datafeed.refresh.RefreshController;
import com.thebluealliance.androidclient.datafeed.refresh.RefreshController.RefreshType;
import com.thebluealliance.androidclient.datafeed.refresh.Refreshable;
import com.thebluealliance.androidclient.datafeed.retrofit.APIv2;
import com.thebluealliance.androidclient.datafeed.DatabaseUpdater;
import com.thebluealliance.androidclient.di.components.FragmentComponent;
import com.thebluealliance.androidclient.di.components.HasFragmentComponent;
import com.thebluealliance.androidclient.models.NoDataViewParams;
import com.thebluealliance.androidclient.subscribers.BaseAPISubscriber;
import com.thebluealliance.androidclient.subscribers.BriteBaseAPISubscriber;
import com.thebluealliance.androidclient.subscribers.EventBusSubscriber;

import org.greenrobot.eventbus.EventBus;

import android.os.Bundle;
import android.support.annotation.VisibleForTesting;
import android.support.v4.app.Fragment;

import java.util.List;

import javax.inject.Inject;

import dagger.Lazy;
import rx.Observable;
import rx.Single;
import rx.Subscription;
import rx.schedulers.Schedulers;

/**
 * Easy abstraction of Fragment datafeed bindings
 *
 * @param <T> Type returned by the API
 * @param <V> Type to be bound to a view
 * @param <S> {@link BaseAPISubscriber} that will take API Data -> prepare data to render
 * @param <B> {@link AbstractDataBinder} that will take prepared data -> view
 */
public abstract class BriteDatafeedFragment
        <T, V, S extends BriteBaseAPISubscriber<T, V>, B extends AbstractDataBinder<V>>
        extends Fragment implements Refreshable {

    @Inject protected S mSubscriber;
    @Inject protected B mBinder;
    @Inject protected EventBus mEventBus;
    @Inject protected Lazy<EventBusSubscriber> mEventBusSubscriber;
    @Inject protected NoDataBinder mNoDataBinder;
    @Inject protected RefreshController mRefreshController;
    @Inject protected Tracker mAnalyticsTracker;
    @Inject protected BriteDatafeed mDatafeed;
    @Inject protected DatabaseUpdater mDatabaseUpdater;

    protected Observable<? extends T> mObservable;
    protected Subscription mSubscription;
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
        mSubscriber.setConsumer(mBinder);
        mSubscriber.setRefreshController(mRefreshController);
        mSubscriber.setRefreshTag(mRefreshTag);
        mSubscriber.setTracker(mAnalyticsTracker);
        mBinder.setActivity(getActivity());
        mBinder.setNoDataBinder(mNoDataBinder);
        mBinder.setNoDataParams(getNoDataParams());
    }

    @Override public void onStart() {
        super.onStart();
        mRefreshController.registerRefreshable(mRefreshTag, this);
        onRefreshStart(RefreshController.NOT_REQUESTED_BY_USER);
    }

    @Override
    public void onResume() {
        super.onResume();
        getNewObservable();
        mRefreshController.registerRefreshable(mRefreshTag, this);
        if (shouldRegisterSubscriberToEventBus()) {
            mEventBus.register(mSubscriber);
        }
        if (shouldRegisterBinderToEventBus()) {
            mEventBus.register(mBinder);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mRefreshController.unregisterRefreshable(mRefreshTag);
        if (mSubscription != null) {
            mSubscription.unsubscribe();
        }
        if (mSubscriber != null) {
            if (shouldRegisterSubscriberToEventBus()) {
                mEventBus.unregister(mSubscriber);
            }
        }
        if (mBinder != null) {
            if (shouldRegisterBinderToEventBus()) {
                mEventBus.unregister(mBinder);
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mSubscriber != null) {
            mSubscriber.onParentStop();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinder.unbind(true);
    }

    /**
     * Allows other things to bind this instance
     */
    public void bind() {
        if (mSubscriber != null) {
            mSubscriber.bindData();
        }
    }

    public boolean isBound() {
        return mBinder != null && mBinder.isDataBound();
    }

    public void setShouldBindImmediately(boolean shouldBind) {
        if (mSubscriber != null) {
            mSubscriber.setShouldBindImmediately(shouldBind);
        }
    }

    public void setShouldBindOnce(boolean shouldBind) {
        if (mSubscriber != null) {
            mSubscriber.setShouldBindOnce(shouldBind);
        }
    }

    /**
     * Registers and subscribes new observables
     */
    private void getNewObservable() {
        if (mSubscriber != null) {
            mObservable = getObservable();
            if (mObservable != null) {
                mSubscription = mObservable.subscribeOn(Schedulers.io())
                        .observeOn(Schedulers.computation())
                        .subscribe(mSubscriber);
            }

        }
    }

    @Override
    public void onRefreshStart(@RefreshType int refreshType) {
        if (mSubscriber != null && mBinder != null) {
            List<Single<Void>> observables = beginDataUpdate(
                    refreshType == RefreshController.REQUESTED_BY_USER
                            ? APIv2.TBA_CACHE_WEB
                            : null);
            NetworkRequestStatusAggregator aggregator = new NetworkRequestStatusAggregator();
            aggregator.addAllRequestStatusObservables(observables);
            mSubscriber.subscribeAndStartNetworkRequestStatusAggregator(aggregator);
            mSubscriber.onRefreshStart(refreshType);
        }
    }

    /**
     * Fragments should inject themselves (to preserve Dagger's strong typing)
     * They just need to have the following line: mComponent.inject(this);
     * Plus, whatever else they want
     * If the types don't match, add the fragment to
     * {@link com.thebluealliance.androidclient.subscribers.SubscriberModule} and rebuild
     * Called in {@link #onCreate(Bundle)}
     */
    protected abstract void inject();

    /**
     * For child to make a call to return the Observable containing the main data model
     * Called in {@link #onResume()}
     */
    protected abstract Observable<? extends T> getObservable();

    /**
     * Brite data fragments separate loading data from the web and displaying it. All displayed
     * data
     * is loaded from the internal database and is updated automatically whenever data in the
     * database changes. This class will be called whenever this fragment should kick off an update
     * of the data it displays by telling another to component to push new data from the web into
     * the internal database. That data should automatically propagate through and be displayed.
     *
     * @param tbaCacheHeader String param to tell the datafeed how to load the data. Use
     *                       {@link APIv2#TBA_CACHE_WEB}, {@link APIv2#TBA_CACHE_LOCAL}, or {@code
     *                       null} for regular usage
     */
    protected abstract List<Single<Void>> beginDataUpdate(String tbaCacheHeader);

    /**
     * @return A string identifying what data this fragment is loading
     */
    protected abstract String getRefreshTag();

    @VisibleForTesting
    public NoDataViewParams getNoDataParams() {
        return null;
    }

    protected boolean shouldRegisterSubscriberToEventBus() {
        return false;
    }

    protected boolean shouldRegisterBinderToEventBus() {
        return false;
    }
}
