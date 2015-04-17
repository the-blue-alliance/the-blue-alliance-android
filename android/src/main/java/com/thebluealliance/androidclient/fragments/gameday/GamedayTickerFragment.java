package com.thebluealliance.androidclient.fragments.gameday;

import android.content.SharedPreferences;
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
import com.firebase.client.Query;
import com.melnykov.fab.FloatingActionButton;
import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.adapters.ListViewAdapter;
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

/**
 * Created by phil on 3/26/15.
 */
public class GamedayTickerFragment extends Fragment implements ChildEventListener {

    private static final String TICKER_FILTER_ENABLED_NOTIFICATIONS = "gameday_ticker_filter_enabled_notificaitons";

    private ListView listView;
    private ListView filterListView;
    private ProgressBar progressBar;
    private ListViewAdapter notificationsAdapter;
    private ListViewAdapter notificationFilterAdapter;
    private Parcelable listState;
    private FloatingActionButton fab;
    Query tickerQuery;
    private int firstVisiblePosition;
    private List<FirebaseNotification> allNotifications = new ArrayList<>();

    private boolean childHasBeenAdded = false;

    public static GamedayTickerFragment newInstance() {
        return new GamedayTickerFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Firebase firebase = new Firebase("https://thebluealliance.firebaseio.com/notifications/"); //TODO move to config file
        tickerQuery = firebase.limitToLast(25);
        tickerQuery.addChildEventListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.list_view_gameday_ticker, null);
        filterListView = (ListView) v.findViewById(R.id.filter_list);

        if (notificationFilterAdapter == null) {
            notificationFilterAdapter = createFilterListAdapter();
        }
        filterListView.setAdapter(notificationFilterAdapter);

        fab = (FloatingActionButton) v.findViewById(R.id.filter_button);
        fab.setOnClickListener(v1 -> onFabClicked());
        listView = (ListView) v.findViewById(R.id.list);

        progressBar = (ProgressBar) v.findViewById(R.id.progress);
        if (childHasBeenAdded) {
            // If view is being recreated and a child has been added, hide the progress bar
            progressBar.setVisibility(View.GONE);
        }

        if (notificationsAdapter != null) {
            listView.setAdapter(notificationsAdapter);
            listView.onRestoreInstanceState(listState);
            listView.setSelection(firstVisiblePosition);
            Log.d("onCreateView", "using existing adapter");
        } else {
            notificationsAdapter = new ListViewAdapter(getActivity(), new ArrayList<>());
            listView.setAdapter(notificationsAdapter);
        }

        return v;
    }

    private void onFabClicked() {
        if (filterListView.getVisibility() != View.VISIBLE) {
            // filter list is currently hidden. show it.
            filterListView.setVisibility(View.VISIBLE);
            // change the fab to a checkbox
            fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_check_white_24dp));
        } else {
            // it's visible. hide it and update the filtered things.
            filterListView.setVisibility(View.GONE);
            updateList();
            // persist the new filter settings
            PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putStringSet(TICKER_FILTER_ENABLED_NOTIFICATIONS, getEnabledNotificationKeys()).apply();
            // change the fab back to the filter icon
            fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_filter_list_white_24dp));
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
        if(enabledNotifications != null) {
            for (ListItem item : listItems) {
                GamedayTickerFilterCheckbox checkbox = (GamedayTickerFilterCheckbox) item;
                if (!enabledNotifications.contains(checkbox.getKey())) {
                    checkbox.setChecked(false);
                }
            }
        }

        return new ListViewAdapter(getActivity(), listItems);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (listView != null) {
            Log.d("onPause", "saving adapter");
            notificationsAdapter = (ListViewAdapter) listView.getAdapter();
            listState = listView.onSaveInstanceState();
            firstVisiblePosition = listView.getFirstVisiblePosition();
        }
    }

    private void updateList() {
        // Collect a list of all enabled notification keys
        final Set<String> enabledNotificationKeys = getEnabledNotificationKeys();

        Observable.from(allNotifications)
                .filter(notification -> enabledNotificationKeys.contains(notification.getNotificationType()))
                .map(FirebaseNotification::getNotification)
                .toList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(notificationsList -> {
                    if (!GamedayTickerFragment.this.isResumed()) {
                        return;
                    }
                    if (notificationsList.isEmpty()) {
                        // Show the "none found" warning
                        progressBar.setVisibility(View.GONE);
                        getView().findViewById(R.id.no_notifications_found).setVisibility(View.VISIBLE);
                        listView.setVisibility(View.GONE);
                    } else {
                        notificationsAdapter.values.clear();
                        notificationsAdapter.values.addAll(notificationsList);
                        notificationsAdapter.notifyDataSetChanged();
                        listView.setVisibility(View.VISIBLE);
                        getView().findViewById(R.id.no_notifications_found).setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }

    private Set<String> getEnabledNotificationKeys() {
        final Set<String> enabledNotificationKeys = new HashSet<>();
        int filterItemCount = filterListView.getAdapter().getCount();
        for (int i = 0; i < filterItemCount; i++) {
            GamedayTickerFilterCheckbox checkbox = ((GamedayTickerFilterCheckbox) filterListView.getAdapter().getItem(i));
            if (checkbox.isChecked()) {
                enabledNotificationKeys.add(checkbox.getKey());
            }
        }
        return enabledNotificationKeys;
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        childHasBeenAdded = true;
        progressBar.setVisibility(View.GONE);
        FirebaseNotification notification = dataSnapshot.getValue(FirebaseNotification.class);
        allNotifications.add(0, notification);
        Log.d(Constants.LOG_TAG, "Json: " + notification.convertToJson());
        // Normaully we would call updateList() to update the list
        // However, that requires rechecking the types of all notificaitons. To be more
        // efficient, we should simply add it to the adapter if the notificaiton's type is
        // currently enabled
        if (getEnabledNotificationKeys().contains(notification.getNotificationType())) {
            notificationsAdapter.values.add(0, notification.getNotification());
            notificationsAdapter.notifyDataSetChanged();
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
