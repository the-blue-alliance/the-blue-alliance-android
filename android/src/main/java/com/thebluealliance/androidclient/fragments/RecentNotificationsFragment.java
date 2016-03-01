package com.thebluealliance.androidclient.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.Utilities;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.itemviews.AllianceSelectionNotificationItemView;
import com.thebluealliance.androidclient.itemviews.AwardsPostedNotificationItemView;
import com.thebluealliance.androidclient.itemviews.GenericNotificationItemView;
import com.thebluealliance.androidclient.models.NoDataViewParams;
import com.thebluealliance.androidclient.models.StoredNotification;
import com.thebluealliance.androidclient.subscribers.RecentNotificationsSubscriber;
import com.thebluealliance.androidclient.viewmodels.AllianceSelectionNotificationViewModel;
import com.thebluealliance.androidclient.viewmodels.AwardsPostedNotificationViewModel;
import com.thebluealliance.androidclient.viewmodels.GenericNotificationViewModel;

import java.util.List;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;
import io.nlopez.smartadapters.SmartAdapter;
import rx.Observable;

public class RecentNotificationsFragment
        extends RecyclerViewFragment<List<StoredNotification>, RecentNotificationsSubscriber> {

    @Inject
    Database mDb;
    @Inject
    EventBus mEventBus;

    @Override
    protected void inject() {
        mComponent.inject(this);
    }

    /**
     * The recent notifications list has to render things a little differently than the normal list.
     * Specifically, we need to remove the dividers between items, adjust the padding, and disable
     * clip-to-padding so that the list's content can scroll beneath the padding. To avoid having
     * to special-case a subclass of DatafeedFragment and inflate a different view, we'll simply
     * override all this stuff programmatically!
     *
     * @param view               The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     *                           from a previous saved state as given here.
     */
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        int pBottom, pLeft, pRight;
        pBottom = mRecyclerView.getPaddingBottom();
        pLeft = mRecyclerView.getPaddingLeft();
        pRight = mRecyclerView.getPaddingRight();
        mRecyclerView.setPadding(pLeft, Utilities.getPixelsFromDp(getContext(), 8), pRight, pBottom);
        mRecyclerView.setClipToPadding(false);
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
    protected boolean shouldRegisterSubscriberToEventBus() {
        return true;
    }

    @Override
    protected String getRefreshTag() {
        return "recentNotifications";
    }

    @Override
    protected NoDataViewParams getNoDataParams() {
        return new NoDataViewParams(R.drawable.ic_notifications_black_48dp, R.string.no_recent_notifications);
    }

    @Override
    public void initializeMaps(SmartAdapter.MultiAdaptersCreator creator) {
        creator.map(AllianceSelectionNotificationViewModel.class, AllianceSelectionNotificationItemView.class)
                .map(AwardsPostedNotificationViewModel.class, AwardsPostedNotificationItemView.class)
                .map(GenericNotificationViewModel.class, GenericNotificationItemView.class);
    }
}
