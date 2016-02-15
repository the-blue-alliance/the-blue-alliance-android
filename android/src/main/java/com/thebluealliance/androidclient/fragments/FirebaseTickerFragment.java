package com.thebluealliance.androidclient.fragments;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.Utilities;
import com.thebluealliance.androidclient.adapters.ListViewAdapter;
import com.thebluealliance.androidclient.database.DatabaseWriter;
import com.thebluealliance.androidclient.di.components.FragmentComponent;
import com.thebluealliance.androidclient.di.components.HasFragmentComponent;
import com.thebluealliance.androidclient.firebase.FirebaseChildType;
import com.thebluealliance.androidclient.firebase.ResumeableRxFirebase;
import com.thebluealliance.androidclient.gcm.notifications.NotificationTypes;
import com.thebluealliance.androidclient.listitems.ListItem;
import com.thebluealliance.androidclient.listitems.gameday.GamedayTickerFilterCheckbox;
import com.thebluealliance.androidclient.models.FirebaseNotification;
import com.thebluealliance.androidclient.views.NoDataView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public abstract class FirebaseTickerFragment extends Fragment implements Action1<List<FirebaseNotification>>, View.OnClickListener {

    private static final String FIREBASE_URL_DEFAULT = "https://thebluealliance.firebaseio.com/";
    private static final int FIREBASE_LOAD_DEPTH_DEFAULT = 25;

    @Inject
    DatabaseWriter mWriter;

    private ListView mListView;
    private ListView mFilterListView;
    private NoDataView mNoDataView;
    private ProgressBar mProgressBar;
    private ListViewAdapter mNotificationsAdapter;
    private ListViewAdapter mNotificationFilterAdapter;
    private Parcelable mListState;
    private int mFirstVisiblePosition;
    private List<FirebaseNotification> mAllNotifications = new ArrayList<>();
    private ResumeableRxFirebase mFirebaseSubscriber;

    private TextView leftButton, rightButton;
    private Set<String> enabledNotifications;
    private boolean filterListShowing;

    private boolean mChildHasBeenAdded = false;
    private String mFirebaseUrl;
    private int mFirebaseLoadDepth;

    protected FragmentComponent mComponent;

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
                .doOnEach(event -> System.out.println("EVENT: " + event.toString()))
                .filter(childEvent -> childEvent != null && childEvent.eventType == FirebaseChildType.CHILD_ADDED)
                .map(childEvent1 -> childEvent1.snapshot.getValue(FirebaseNotification.class))
                .buffer(5)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this);
        Firebase.setAndroidContext(getActivity());
        Firebase ticker = new Firebase(mFirebaseUrl);
        ticker.limitToLast(mFirebaseLoadDepth).addChildEventListener(mFirebaseSubscriber);
        ticker.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() == 0) {
                    mNoDataView.setVisibility(View.VISIBLE);
                    mNoDataView.setText(R.string.firebase_empty_ticker);
                    mListView.setVisibility(View.GONE);
                    mProgressBar.setVisibility(View.GONE);
                } else {
                    mNoDataView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                mNoDataView.setVisibility(View.VISIBLE);
                mNoDataView.setText(R.string.firebase_error);
                mListView.setVisibility(View.GONE);
                mProgressBar.setVisibility(View.GONE);
            }
        });

        filterListShowing = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_firebase_ticker, null);
        mFilterListView = (ListView) v.findViewById(R.id.filter_list);

        mNoDataView = (NoDataView) v.findViewById(R.id.no_data);
        mNoDataView.setImage(R.drawable.ic_notifications_black_48dp);

        if (mNotificationFilterAdapter == null) {
            mNotificationFilterAdapter = createFilterListAdapter();
        }
        mFilterListView.setAdapter(mNotificationFilterAdapter);

        leftButton = (TextView) v.findViewById(R.id.left_button);
        rightButton = (TextView) v.findViewById(R.id.right_button);
        leftButton.setOnClickListener(this);
        rightButton.setOnClickListener(this);

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

        setUpFilterListViews();

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

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.right_button) {
            if (!filterListShowing) {
                filterListShowing = true;
                setUpFilterListViews();
                // Save the initially checked items
                enabledNotifications = getEnabledNotificationKeys();
            } else {
                filterListShowing = false;
                setUpFilterListViews();
                // Apply the filter
                updateList();
            }
        } else if (id == R.id.left_button) {
            filterListShowing = false;
            setUpFilterListViews();
            // Reset the filter options
            mNotificationFilterAdapter = createFilterListAdapter(enabledNotifications);
            mFilterListView.setAdapter(mNotificationFilterAdapter);
        }
    }

    private void setUpFilterListViews() {
        if (filterListShowing) {
            // Show the filter list
            mFilterListView.setVisibility(View.VISIBLE);
            // Show the left button
            leftButton.setVisibility(View.VISIBLE);
            leftButton.setText(R.string.firebase_cancel);
            // Set up right button
            rightButton.setText(R.string.firebase_apply_filter);
        } else {
            // Hide the filter list
            mFilterListView.setVisibility(View.GONE);
            // Hide the left button
            leftButton.setVisibility(View.GONE);
            // Update the right button
            rightButton.setText(R.string.firebase_filter);
        }
    }

    private ListViewAdapter createFilterListAdapter(Set<String> enabledNotifications) {
        List<ListItem> listItems = new ArrayList<>();

        // Start with all notifications enabled
        listItems.add(new GamedayTickerFilterCheckbox(R.layout.list_item_checkbox_upcoming_match, "Upcoming Matches", NotificationTypes.UPCOMING_MATCH, true));
        listItems.add(new GamedayTickerFilterCheckbox(R.layout.list_item_checkbox_match_results, "Match Results", NotificationTypes.MATCH_SCORE, true));
        listItems.add(new GamedayTickerFilterCheckbox(R.layout.list_item_checkbox_schedule_updated, "Schedule Updated", NotificationTypes.SCHEDULE_UPDATED, true));
        listItems.add(new GamedayTickerFilterCheckbox(R.layout.list_item_checkbox_competition_level_starting, "Competition Level Starting", NotificationTypes.LEVEL_STARTING, true));
        listItems.add(new GamedayTickerFilterCheckbox(R.layout.list_item_checkbox_alliance_selections, "Alliance Selections", NotificationTypes.ALLIANCE_SELECTION, true));
        listItems.add(new GamedayTickerFilterCheckbox(R.layout.list_item_checkbox_awards_posted, "Awards Posted", NotificationTypes.AWARDS, true));

        // Initialize the preferences to their appropriate value
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

    private ListViewAdapter createFilterListAdapter() {
        return createFilterListAdapter(null);
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
                        mProgressBar.setVisibility(View.GONE);
                        mListView.setVisibility(View.GONE);
                        mNoDataView.setVisibility(View.VISIBLE);
                        mNoDataView.setText(R.string.firebase_no_matching_items);
                    } else {
                        mNotificationsAdapter.values.clear();
                        mNotificationsAdapter.values.addAll(notificationsList);
                        mNotificationsAdapter.notifyDataSetChanged();
                        mListView.setVisibility(View.VISIBLE);
                        mNoDataView.setVisibility(View.GONE);
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
        }
        updateList();
    }

}
