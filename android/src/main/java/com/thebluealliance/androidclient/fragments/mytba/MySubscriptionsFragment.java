package com.thebluealliance.androidclient.fragments.mytba;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.datafeed.MyTbaDatafeed;
import com.thebluealliance.androidclient.fragments.ListViewFragment;
import com.thebluealliance.androidclient.models.NoDataViewParams;
import com.thebluealliance.androidclient.models.Subscription;
import com.thebluealliance.androidclient.subscribers.SubscriptionListSubscriber;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;

public class MySubscriptionsFragment
  extends ListViewFragment<List<Subscription>, SubscriptionListSubscriber> {

    @Inject MyTbaDatafeed mMyTbaDatafeed;

    public static MySubscriptionsFragment newInstance() {
        return new MySubscriptionsFragment();
    }

    @Override
    protected void inject() {
        mComponent.inject(this);
    }

    @Override
    protected Observable<List<Subscription>> getObservable(String tbaCacheHeader) {
        return mMyTbaDatafeed.fetchLocalSubscriptions();
    }

    @Override
    protected String getRefreshTag() {
        return "mySubscriptions";
    }

    @Override public NoDataViewParams getNoDataParams() {
        return new NoDataViewParams(R.drawable.ic_notifications_black_48dp, R.string.no_subscription_data);
    }
}
