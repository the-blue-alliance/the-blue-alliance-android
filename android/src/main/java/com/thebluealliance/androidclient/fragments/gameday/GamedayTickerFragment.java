package com.thebluealliance.androidclient.fragments.gameday;

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

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.melnykov.fab.FloatingActionButton;
import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.Utilities;
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

    private static final String FIREBASE_URL_DEFAULT = "https://thebluealliance.firebaseio.com/notifications/";
    private static final int FIREBASE_LOAD_DEPTH_DEFAULT = 25;

    private ListView listView;
    private ListView filterListView;
    private ProgressBar progressBar;
    private ListViewAdapter adapter;
    private Parcelable listState;
    private FloatingActionButton fab;
    Firebase ticker;
    private int firstVisiblePosition;
    private List<FirebaseNotification> allNotifications = new ArrayList<>();

    private boolean childHasBeenAdded = false;
    private String firebaseUrl;
    private int firebaseLoadDepth;

    public static GamedayTickerFragment newInstance() {
        return new GamedayTickerFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadFirebaseParams();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.list_view_gameday_ticker, null);
        filterListView = (ListView) v.findViewById(R.id.filter_list);
        setUpFilterList();
        fab = (FloatingActionButton) v.findViewById(R.id.filter_button);
        fab.setOnClickListener(v1 -> onFabClicked());
        listView = (ListView) v.findViewById(R.id.list);
        progressBar = (ProgressBar) v.findViewById(R.id.progress);
        if (adapter != null) {
            listView.setAdapter(adapter);
            listView.onRestoreInstanceState(listState);
            listView.setSelection(firstVisiblePosition);
            Log.d("onCreateView", "using existing adapter");
        } else {
            adapter = new ListViewAdapter(getActivity(), new ArrayList<>());
            listView.setAdapter(adapter);
        }
        return v;
    }

    private void onFabClicked() {
        if(filterListView.getVisibility() != View.VISIBLE) {
            // filter list is currently hidden. show it.
            filterListView.setVisibility(View.VISIBLE);
            // change the fab to a checkbox
            fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_check_white_24dp));
        } else {
            // it's visible. hide it and update the filtered things.
            filterListView.setVisibility(View.GONE);
            updateList();
            // change the fab back to the filter icon
            fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_filter_list_white_24dp));
        }
    }

    private void setUpFilterList() {
        List<ListItem> listItems = new ArrayList<>();

        listItems.add(new GamedayTickerFilterCheckbox(R.layout.list_item_checkbox_upcoming_match, "Upcoming Matches", NotificationTypes.UPCOMING_MATCH, true));
        listItems.add(new GamedayTickerFilterCheckbox(R.layout.list_item_checkbox_match_results, "Match Results", NotificationTypes.MATCH_SCORE, true));
        listItems.add(new GamedayTickerFilterCheckbox(R.layout.list_item_checkbox_schedule_updated, "Schedule Updated", NotificationTypes.SCHEDULE_UPDATED, true));
        listItems.add(new GamedayTickerFilterCheckbox(R.layout.list_item_checkbox_competition_level_starting, "Competition Level Starting", NotificationTypes.LEVEL_STARTING, true));
        listItems.add(new GamedayTickerFilterCheckbox(R.layout.list_item_checkbox_alliance_selections, "Alliance Selections", NotificationTypes.ALLIANCE_SELECTION, true));
        listItems.add(new GamedayTickerFilterCheckbox(R.layout.list_item_checkbox_awards_posted, "Awards Posted", NotificationTypes.AWARDS, true));

        ListViewAdapter adapter = new ListViewAdapter(getActivity(), listItems);
        filterListView.setAdapter(adapter);
    }

    private void loadFirebaseParams(){
        firebaseUrl = Utilities.readLocalProperty(getActivity(), "firebase.url", FIREBASE_URL_DEFAULT);
        String loadDepthTemp = Utilities.readLocalProperty(getActivity(), "firebase.depth", Integer.toString(FIREBASE_LOAD_DEPTH_DEFAULT));

        try {
            firebaseLoadDepth = Integer.parseInt(loadDepthTemp);
        }catch(NumberFormatException e){
            firebaseLoadDepth = FIREBASE_LOAD_DEPTH_DEFAULT;
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        ticker = new Firebase(firebaseUrl);
        ticker.limitToLast(firebaseLoadDepth).addChildEventListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (listView != null) {
            Log.d("onPause", "saving adapter");
            adapter = (ListViewAdapter) listView.getAdapter();
            listState = listView.onSaveInstanceState();
            firstVisiblePosition = listView.getFirstVisiblePosition();
        }
    }

    private void updateList() {
        // Collect a list of all enabled notification keys
        final Set<String> enabledNotificationKeys = new HashSet<>();
        int filterItemCount = filterListView.getAdapter().getCount();
        for(int i = 0; i < filterItemCount; i++) {
            GamedayTickerFilterCheckbox checkbox = ((GamedayTickerFilterCheckbox) filterListView.getAdapter().getItem(i));
            if(checkbox.isChecked()) {
                enabledNotificationKeys.add(checkbox.getKey());
            }
        }

        Observable.from(allNotifications)
                .filter(notification -> enabledNotificationKeys.contains(notification.getNotificationType()))
                .map(FirebaseNotification::getNotification)
                .toList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(notificationsList -> {
                    if (!GamedayTickerFragment.this.isResumed() || getView() == null) {
                        return;
                    }
                    if (notificationsList.isEmpty()) {
                        // Show the "none found" warning
                        progressBar.setVisibility(View.GONE);
                        getView().findViewById(R.id.no_notifications_found).setVisibility(View.VISIBLE);
                        listView.setVisibility(View.GONE);
                    } else {
                        /* Update the listview adapter */
                        adapter.values.clear();
                        adapter.values.addAll(notificationsList);
                        adapter.notifyDataSetChanged();
                        listView.setVisibility(View.VISIBLE);
                        getView().findViewById(R.id.no_notifications_found).setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        childHasBeenAdded = true;
        Log.d(Constants.LOG_TAG, "Adding ticker item with key "+dataSnapshot.getKey());
        FirebaseNotification notification = dataSnapshot.getValue(FirebaseNotification.class);
        allNotifications.add(0, notification);
        updateList();
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
