package com.thebluealliance.androidclient.fragments;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.models.NoDataViewParams;
import com.thebluealliance.androidclient.models.StoredNotification;
import com.thebluealliance.androidclient.subscribers.RecentNotificationsSubscriber;

import java.util.List;

import rx.Observable;

/**
 * Created by Nathan on 8/9/15.
 */
public class RecentNotificationsFragmentV2 extends ListViewFragment<List<StoredNotification>, RecentNotificationsSubscriber> {

    @Override
    protected void inject() {
        mComponent.inject(this);
    }

    @Override
    protected Observable<List<StoredNotification>> getObservable() {
        return Observable.just(Database.getInstance(getActivity()).getNotificationsTable().get());
    }

    @Override
    protected NoDataViewParams getNoDataParams() {
        return new NoDataViewParams(R.drawable.ic_notifications_black_48dp, R.string.no_recent_notifications);
    }
}
