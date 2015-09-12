package com.thebluealliance.androidclient.fragments;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.models.NoDataViewParams;
import com.thebluealliance.androidclient.models.StoredNotification;
import com.thebluealliance.androidclient.subscribers.RecentNotificationsSubscriber;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;

public class RecentNotificationsFragment
  extends ListViewFragment<List<StoredNotification>, RecentNotificationsSubscriber> {

    @Inject Database mDb;

    @Override
    protected void inject() {
        mComponent.inject(this);
    }

    @Override
    protected Observable<List<StoredNotification>> getObservable(String tbaCacheHeader) {
        return Observable.create((observer) -> {
            try {
                observer.onNext(mDb.getNotificationsTable().get());
                observer.onCompleted();
            } catch (Exception e) {
                observer.onError(e);
            }
        });
    }

    @Override
    protected String getRefreshTag() {
        return "recentNotifications";
    }

    @Override
    protected NoDataViewParams getNoDataParams() {
        return new NoDataViewParams(R.drawable.ic_notifications_black_48dp, R.string.no_recent_notifications);
    }
}
