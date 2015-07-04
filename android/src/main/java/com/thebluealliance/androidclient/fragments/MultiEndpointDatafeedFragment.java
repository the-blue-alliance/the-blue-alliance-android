package com.thebluealliance.androidclient.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.thebluealliance.androidclient.binders.AbstractDataBinder;
import com.thebluealliance.androidclient.datafeed.CacheableDatafeed;
import com.thebluealliance.androidclient.modules.SubscriberModule;
import com.thebluealliance.androidclient.modules.components.FragmentComponent;
import com.thebluealliance.androidclient.modules.components.HasFragmentComponent;
import com.thebluealliance.androidclient.subscribers.BaseAPISubscriber;
import com.thebluealliance.androidclient.subscribers.MultiEndpointSubscriber;

import java.util.List;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;
import rx.Observable;
import rx.schedulers.Schedulers;

public abstract class MultiEndpointDatafeedFragment
  <V, S extends BaseAPISubscriber<?, V>, B extends AbstractDataBinder<V>> extends Fragment {
    @Inject protected S mSubscriber;
    @Inject protected B mBinder;
    @Inject protected EventBus mEventBus;
    @Inject protected MultiEndpointSubscriber mMultiSubscriber;

    protected CacheableDatafeed mDatafeed;
    protected List<Observable> mObservables;
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
    }

    @Override
    public void onResume() {
        super.onResume();
        mObservables = getObservables();
        for (int i = 0; i < mObservables.size(); i++) {
            mObservables.get(i).subscribeOn(Schedulers.io())
              .observeOn(Schedulers.computation())
              .subscribe(mMultiSubscriber);
        }
        if (mSubscriber != null) {
            mEventBus.register(mSubscriber);
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
    protected abstract List<Observable> getObservables();
}
