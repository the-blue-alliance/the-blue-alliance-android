package com.thebluealliance.androidclient.fragments;

import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.binders.RecentNotificationsListBinder;
import com.thebluealliance.androidclient.binders.RecyclerViewBinder;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.databinding.FragmentRecentNotificationsBinding;
import com.thebluealliance.androidclient.itemviews.AllianceSelectionNotificationItemView;
import com.thebluealliance.androidclient.itemviews.AwardsPostedNotificationItemView;
import com.thebluealliance.androidclient.itemviews.CompLevelStartingNotificationItemView;
import com.thebluealliance.androidclient.itemviews.GenericNotificationItemView;
import com.thebluealliance.androidclient.itemviews.ScheduleUpdatedNotificationItemView;
import com.thebluealliance.androidclient.itemviews.ScoreNotificationItemView;
import com.thebluealliance.androidclient.itemviews.UpcomingMatchNotificationItemView;
import com.thebluealliance.androidclient.models.NoDataViewParams;
import com.thebluealliance.androidclient.models.StoredNotification;
import com.thebluealliance.androidclient.subscribers.RecentNotificationsSubscriber;
import com.thebluealliance.androidclient.viewmodels.AllianceSelectionNotificationViewModel;
import com.thebluealliance.androidclient.viewmodels.AwardsPostedNotificationViewModel;
import com.thebluealliance.androidclient.viewmodels.CompLevelStartingNotificationViewModel;
import com.thebluealliance.androidclient.viewmodels.GenericNotificationViewModel;
import com.thebluealliance.androidclient.viewmodels.ScheduleUpdatedNotificationViewModel;
import com.thebluealliance.androidclient.viewmodels.ScoreNotificationViewModel;
import com.thebluealliance.androidclient.viewmodels.UpcomingMatchNotificationViewModel;
import com.thebluealliance.androidclient.views.NoDataView;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import io.nlopez.smartadapters.SmartAdapter;
import rx.Observable;

@AndroidEntryPoint
public class RecentNotificationsFragment
        extends DatafeedFragment<List<StoredNotification>, List<Object>, FragmentRecentNotificationsBinding, RecentNotificationsSubscriber, RecentNotificationsListBinder>
        implements RecyclerViewBinder.RecyclerViewAdapterCreatorInitializer {

    @Inject Database mDb;

    private Parcelable mListState;
    private RecyclerView.Adapter mAdapter;
    protected RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;

    @Override
    public
    @Nullable
    View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_recent_notifications, null);

        mBinder.setRootView(v);
        mBinder.setRecyclerViewBinderMapper(this);

        mRecyclerView = (RecyclerView) v.findViewById(R.id.list);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        ProgressBar progressBar = (ProgressBar) v.findViewById(R.id.progress);
        if (mAdapter != null) {
            mRecyclerView.setAdapter(mAdapter);
            mLayoutManager.onRestoreInstanceState(mListState);
            progressBar.setVisibility(View.GONE);
        }

        mBinder.setNoDataView((NoDataView) v.findViewById(R.id.no_data));
        return v;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mRecyclerView != null) {
            mAdapter = mRecyclerView.getAdapter();
            mListState = mLayoutManager.onSaveInstanceState();
        }
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
    protected boolean shouldRegisterBinderToEventBus() {
        return true;
    }

    @Override
    protected String getRefreshTag() {
        return "recentNotifications";
    }

    @Override public NoDataViewParams getNoDataParams() {
        return new NoDataViewParams(R.drawable.ic_notifications_black_48dp, R.string.no_recent_notifications);
    }

    @Override
    public void initializeAdapterCreator(SmartAdapter.MultiAdaptersCreator creator) {
        creator.map(AllianceSelectionNotificationViewModel.class, AllianceSelectionNotificationItemView.class)
                .map(AwardsPostedNotificationViewModel.class, AwardsPostedNotificationItemView.class)
                .map(CompLevelStartingNotificationViewModel.class, CompLevelStartingNotificationItemView.class)
                .map(UpcomingMatchNotificationViewModel.class, UpcomingMatchNotificationItemView.class)
                .map(ScoreNotificationViewModel.class, ScoreNotificationItemView.class)
                .map(ScheduleUpdatedNotificationViewModel.class, ScheduleUpdatedNotificationItemView.class)
                .map(GenericNotificationViewModel.class, GenericNotificationItemView.class);
    }
}
