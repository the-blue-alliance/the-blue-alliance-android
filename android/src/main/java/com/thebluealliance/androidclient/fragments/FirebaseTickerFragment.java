package com.thebluealliance.androidclient.fragments;

import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.firebase.client.Firebase;
import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.Utilities;
import com.thebluealliance.androidclient.activities.GamedayActivity;
import com.thebluealliance.androidclient.adapters.ListViewAdapter;
import com.thebluealliance.androidclient.database.DatabaseWriter;
import com.thebluealliance.androidclient.di.components.FragmentComponent;
import com.thebluealliance.androidclient.di.components.HasFragmentComponent;
import com.thebluealliance.androidclient.firebase.FirebaseChildType;
import com.thebluealliance.androidclient.firebase.ResumeableRxFirebase;
import com.thebluealliance.androidclient.gcm.notifications.BaseNotification;
import com.thebluealliance.androidclient.gcm.notifications.NotificationTypes;
import com.thebluealliance.androidclient.listitems.ListItem;
import com.thebluealliance.androidclient.listitems.gameday.GamedayTickerFilterCheckbox;
import com.thebluealliance.androidclient.models.FirebaseNotification;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public abstract class FirebaseTickerFragment extends Fragment implements Action1<List<FirebaseNotification>> {

    private static final String TICKER_FILTER_ENABLED_NOTIFICATIONS = "gameday_ticker_filter_enabled_notificaitons";

    private static final String FIREBASE_URL_DEFAULT = "https://thebluealliance.firebaseio.com/";
    private static final int FIREBASE_LOAD_DEPTH_DEFAULT = 25;

    @Inject DatabaseWriter mWriter;

    private ListView mListView;
    private ListView mFilterListView;
    private ProgressBar mProgressBar;
    private ListViewAdapter mNotificationsAdapter;
    private ListViewAdapter mNotificationFilterAdapter;
    private Parcelable mListState;
    private FloatingActionButton mFab;
    private int mFirstVisiblePosition;
    private List<FirebaseNotification> mAllNotifications = new ArrayList<>();
    private ResumeableRxFirebase mFirebaseSubscriber;

    private boolean mChildHasBeenAdded = false;
    private String mFirebaseUrl;
    private int mFirebaseLoadDepth;

    protected  FragmentComponent mComponent;

    protected abstract void inject();

    protected abstract String getFirebaseUrlSuffix();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActivity() instanceof HasFragmentComponent) {
            mComponent = ((HasFragmentComponent) getActivity()).getComponent();
        }
        inject();
        loadFirebaseParams();
        mFirebaseSubscriber = new ResumeableRxFirebase();
        // Delivery will be resumed once the view hierarchy is created
        mFirebaseSubscriber.pauseDelivery();
        mFirebaseSubscriber.getObservable()
          .filter(childEvent -> childEvent != null && childEvent.eventType == FirebaseChildType.CHILD_ADDED)
          .map(childEvent1 -> childEvent1.snapshot.getValue(FirebaseNotification.class))
          .buffer(5)
          .subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(this);
        Firebase.setAndroidContext(getActivity());
        Firebase ticker = new Firebase(mFirebaseUrl);
        ticker.limitToLast(mFirebaseLoadDepth).addChildEventListener(mFirebaseSubscriber);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.list_view_gameday_ticker, null);
        mFilterListView = (ListView) v.findViewById(R.id.filter_list);

        if (mNotificationFilterAdapter == null) {
            mNotificationFilterAdapter = createFilterListAdapter();
        }
        mFilterListView.setAdapter(mNotificationFilterAdapter);

        mFab = ((GamedayActivity) getActivity()).getmFab();
        mFab.setOnClickListener(v1 -> onFabClicked());
        mListView = (ListView) v.findViewById(R.id.list);
        mProgressBar = (ProgressBar) v.findViewById(R.id.progress);
        if (mChildHasBeenAdded) {
            // If view is being recreated and a child has been added, hide the progress bar
            mProgressBar.setVisibility(View.GONE);
        }

        if (mNotificationsAdapter != null) {
            mListView.setAdapter(mNotificationsAdapter);
            mListView.onRestoreInstanceState(mListState);
            mListView.setSelection(mFirstVisiblePosition);
            Log.d("onCreateView", "using existing adapter");
        } else {
            mNotificationsAdapter = new ListViewAdapter(getActivity(), new ArrayList<>());
            mListView.setAdapter(mNotificationsAdapter);
        }
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        mFirebaseSubscriber.resumeDelivery();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mListView != null) {
            Log.d("onPause", "saving adapter");
            mNotificationsAdapter = (ListViewAdapter) mListView.getAdapter();
            mListState = mListView.onSaveInstanceState();
            mFirstVisiblePosition = mListView.getFirstVisiblePosition();
        }
        mFirebaseSubscriber.pauseDelivery();
    }

    private void onFabClicked() {
        if (mFilterListView.getVisibility() != View.VISIBLE) {
            // filter list is currently hidden. show it.
            mFilterListView.setVisibility(View.VISIBLE);
            // change the fab to a checkbox
            mFab.setImageDrawable(getResources().getDrawable(R.drawable.ic_check_white_24dp));
        } else {
            // it's visible. hide it and update the filtered things.
            mFilterListView.setVisibility(View.GONE);
            updateList();
            // persist the new filter settings
            PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putStringSet(TICKER_FILTER_ENABLED_NOTIFICATIONS, getEnabledNotificationKeys()).apply();
            // change the fab back to the filter icon
            mFab.setImageDrawable(getResources().getDrawable(R.drawable.ic_filter_list_white_24dp));
        }
    }

    private ListViewAdapter createFilterListAdapter() {
        List<ListItem> listItems = new ArrayList<>();

        // Start with all notifications enabled
        listItems.add(new GamedayTickerFilterCheckbox(R.layout.list_item_checkbox_upcoming_match, "Upcoming Matches", NotificationTypes.UPCOMING_MATCH, true));
        listItems.add(new GamedayTickerFilterCheckbox(R.layout.list_item_checkbox_match_results, "Match Results", NotificationTypes.MATCH_SCORE, true));
        listItems.add(new GamedayTickerFilterCheckbox(R.layout.list_item_checkbox_schedule_updated, "Schedule Updated", NotificationTypes.SCHEDULE_UPDATED, true));
        listItems.add(new GamedayTickerFilterCheckbox(R.layout.list_item_checkbox_competition_level_starting, "Competition Level Starting", NotificationTypes.LEVEL_STARTING, true));
        listItems.add(new GamedayTickerFilterCheckbox(R.layout.list_item_checkbox_alliance_selections, "Alliance Selections", NotificationTypes.ALLIANCE_SELECTION, true));
        listItems.add(new GamedayTickerFilterCheckbox(R.layout.list_item_checkbox_awards_posted, "Awards Posted", NotificationTypes.AWARDS, true));

        // Retrieve the stored filter preference
        // On the first time launching gameday, that preference will be null, so we must account for that
        Set<String> enabledNotifications = PreferenceManager.getDefaultSharedPreferences(getActivity()).getStringSet(TICKER_FILTER_ENABLED_NOTIFICATIONS, null);
        if (enabledNotifications != null) {
            for (ListItem item : listItems) {
                GamedayTickerFilterCheckbox checkbox = (GamedayTickerFilterCheckbox) item;
                if (!enabledNotifications.contains(checkbox.getKey())) {
                    checkbox.setChecked(false);
                }
            }
        }

        return new ListViewAdapter(getActivity(), listItems);
    }

    private void loadFirebaseParams() {
        String firebaseBase = Utilities.readLocalProperty(getActivity(), "firebase.url", FIREBASE_URL_DEFAULT);
        mFirebaseUrl = firebaseBase + getFirebaseUrlSuffix();
        String loadDepthTemp = Utilities.readLocalProperty(getActivity(), "firebase.depth", Integer.toString(FIREBASE_LOAD_DEPTH_DEFAULT));

        try {
            mFirebaseLoadDepth = Integer.parseInt(loadDepthTemp);
        } catch (NumberFormatException e) {
            mFirebaseLoadDepth = FIREBASE_LOAD_DEPTH_DEFAULT;
        }
    }


    private void updateList() {
        // Collect a list of all enabled notification keys
        final Set<String> enabledNotificationKeys = getEnabledNotificationKeys();

        Observable.from(mAllNotifications)
                .filter(notification -> enabledNotificationKeys.contains(notification.getNotificationType()))
                .map(firebaseNotification -> {
                    firebaseNotification.setDatabaseWriter(mWriter);
                    return firebaseNotification.getNotification();
                })
                .filter(n -> n != null)
                .toList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(notificationsList -> {
                    if (!FirebaseTickerFragment.this.isResumed() || getView() == null) {
                        return;
                    }
                    if (notificationsList.isEmpty()) {
                        // Show the "none found" warning
                        // TODO: Switch to using NoDataView
                        mProgressBar.setVisibility(View.GONE);
                        getView().findViewById(R.id.no_notifications_found).setVisibility(View.VISIBLE);
                        mListView.setVisibility(View.GONE);
                    } else {
                        mNotificationsAdapter.values.clear();
                        mNotificationsAdapter.values.addAll(notificationsList);
                        mNotificationsAdapter.notifyDataSetChanged();
                        mListView.setVisibility(View.VISIBLE);
                        getView().findViewById(R.id.no_notifications_found).setVisibility(View.GONE);
                        mProgressBar.setVisibility(View.GONE);
                    }
                });
    }

    private Set<String> getEnabledNotificationKeys() {
        final Set<String> enabledNotificationKeys = new HashSet<>();
        int filterItemCount = mFilterListView.getAdapter().getCount();
        for (int i = 0; i < filterItemCount; i++) {
            GamedayTickerFilterCheckbox checkbox = ((GamedayTickerFilterCheckbox) mFilterListView.getAdapter().getItem(i));
            if (checkbox.isChecked()) {
                enabledNotificationKeys.add(checkbox.getKey());
            }
        }
        return enabledNotificationKeys;
    }

    public void call(List<FirebaseNotification> firebaseNotifications) {
        mChildHasBeenAdded = true;
        mProgressBar.setVisibility(View.GONE);
        for (FirebaseNotification firebaseNotification : firebaseNotifications) {
            firebaseNotification.setDatabaseWriter(mWriter);
            mAllNotifications.add(0, firebaseNotification);

            // Normally we would call updateList() to update the list
            // However, that requires rechecking the types of all notificaitons. To be more
            // efficient, we should simply add it to the adapter if the notificaiton's type is
            // currently enabled
            if (getEnabledNotificationKeys().contains(firebaseNotification.getNotificationType())) {
                BaseNotification notification = firebaseNotification.getNotification();
                if (notification != null) {
                    Log.d(Constants.LOG_TAG, "Adding ticker item with key " + notification.getKey());
                    mNotificationsAdapter.values.add(0, notification);
                    mNotificationsAdapter.notifyDataSetChanged();
                }
            }
        }
    }

}
