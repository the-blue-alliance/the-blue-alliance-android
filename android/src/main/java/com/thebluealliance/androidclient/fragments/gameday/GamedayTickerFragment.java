package com.thebluealliance.androidclient.fragments.gameday;

import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.melnykov.fab.FloatingActionButton;
import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.Utilities;
import com.thebluealliance.androidclient.activities.GamedayActivity;
import com.thebluealliance.androidclient.adapters.ListViewAdapter;
import com.thebluealliance.androidclient.firebase.BufferedChildEventListener;
import com.thebluealliance.androidclient.gcm.notifications.BaseNotification;
import com.thebluealliance.androidclient.gcm.notifications.NotificationTypes;
import com.thebluealliance.androidclient.listitems.ListItem;
import com.thebluealliance.androidclient.listitems.gameday.GamedayTickerFilterCheckbox;
import com.thebluealliance.androidclient.models.FirebaseNotification;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

public class GamedayTickerFragment extends Fragment implements ChildEventListener {

    private static final String TICKER_FILTER_ENABLED_NOTIFICATIONS = "gameday_ticker_filter_enabled_notificaitons";

    private static final String FIREBASE_URL_DEFAULT = "https://thebluealliance.firebaseio.com/notifications/";
    private static final int FIREBASE_LOAD_DEPTH_DEFAULT = 25;

    private ListView mListView;
    private ListView mFilterListView;
    private ProgressBar mProgressBar;
    private ListViewAdapter mNotificationsAdapter;
    private ListViewAdapter mNotificationFilterAdapter;
    private Parcelable mListState;
    private FloatingActionButton mFab;
    private int mFirstVisiblePosition;
    private List<FirebaseNotification> mAllNotifications = new ArrayList<>();
    private BufferedChildEventListener mChildEventListener;

    private boolean mChildHasBeenAdded = false;
    private String mFirebaseUrl;
    private int mFirebaseLoadDepth;

    public static GamedayTickerFragment newInstance() {
        return new GamedayTickerFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadFirebaseParams();
        mChildEventListener = new BufferedChildEventListener(this);
        // Delivery will be resumed once the view hierarchy is created
        mChildEventListener.pauseDelivery();
        Firebase.setAndroidContext(getActivity());
        Firebase ticker = new Firebase(mFirebaseUrl);
        ticker.limitToLast(mFirebaseLoadDepth).addChildEventListener(mChildEventListener);
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
        mChildEventListener.resumeDelivery();
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
        mChildEventListener.pauseDelivery();
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

        // Start with all notificaitons enabled
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
        mFirebaseUrl = Utilities.readLocalProperty(getActivity(), "firebase.url", FIREBASE_URL_DEFAULT);
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
                .map(FirebaseNotification::getNotification)
                .filter(n -> n != null)
                .toList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(notificationsList -> {
                    if (!GamedayTickerFragment.this.isResumed() || getView() == null) {
                        return;
                    }
                    if (notificationsList.isEmpty()) {
                        // Show the "none found" warning
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

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        mChildHasBeenAdded = true;
        Log.d(Constants.LOG_TAG, "Adding ticker item with key " + dataSnapshot.getKey());
        mProgressBar.setVisibility(View.GONE);
        FirebaseNotification notification = dataSnapshot.getValue(FirebaseNotification.class);
        mAllNotifications.add(0, notification);
        Log.d(Constants.LOG_TAG, "Json: " + notification.convertToJson());
        // Normally we would call updateList() to update the list
        // However, that requires rechecking the types of all notificaitons. To be more
        // efficient, we should simply add it to the adapter if the notificaiton's type is
        // currently enabled
        if (getEnabledNotificationKeys().contains(notification.getNotificationType())) {
            BaseNotification n = notification.getNotification();
            if (n != null) {
                mNotificationsAdapter.values.add(0, n);
                mNotificationsAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {

    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onCancelled(FirebaseError firebaseError) {

    }
}
