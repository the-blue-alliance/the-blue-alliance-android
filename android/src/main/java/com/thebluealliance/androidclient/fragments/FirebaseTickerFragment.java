package com.thebluealliance.androidclient.fragments;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import com.firebase.client.Firebase;
import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.Utilities;
import com.thebluealliance.androidclient.adapters.AnimatedRecyclerMultiAdapter;
import com.thebluealliance.androidclient.adapters.ListViewAdapter;
import com.thebluealliance.androidclient.database.DatabaseWriter;
import com.thebluealliance.androidclient.datafeed.retrofit.FirebaseAPI;
import com.thebluealliance.androidclient.di.components.FragmentComponent;
import com.thebluealliance.androidclient.di.components.HasFragmentComponent;
import com.thebluealliance.androidclient.firebase.FirebaseChildType;
import com.thebluealliance.androidclient.firebase.ResumeableRxFirebase;
import com.thebluealliance.androidclient.gcm.notifications.BaseNotification;
import com.thebluealliance.androidclient.gcm.notifications.NotificationTypes;
import com.thebluealliance.androidclient.itemviews.AllianceSelectionNotificationItemView;
import com.thebluealliance.androidclient.itemviews.AwardsPostedNotificationItemView;
import com.thebluealliance.androidclient.itemviews.CompLevelStartingNotificationItemView;
import com.thebluealliance.androidclient.itemviews.GenericNotificationItemView;
import com.thebluealliance.androidclient.itemviews.ScheduleUpdatedNotificationItemView;
import com.thebluealliance.androidclient.itemviews.ScoreNotificationItemView;
import com.thebluealliance.androidclient.itemviews.UpcomingMatchNotificationItemView;
import com.thebluealliance.androidclient.listitems.ListItem;
import com.thebluealliance.androidclient.listitems.gameday.GamedayTickerFilterCheckbox;
import com.thebluealliance.androidclient.models.FirebaseNotification;
import com.thebluealliance.androidclient.viewmodels.AllianceSelectionNotificationViewModel;
import com.thebluealliance.androidclient.viewmodels.AwardsPostedNotificationViewModel;
import com.thebluealliance.androidclient.viewmodels.CompLevelStartingNotificationViewModel;
import com.thebluealliance.androidclient.viewmodels.GenericNotificationViewModel;
import com.thebluealliance.androidclient.viewmodels.ScheduleUpdatedNotificationViewModel;
import com.thebluealliance.androidclient.viewmodels.ScoreNotificationViewModel;
import com.thebluealliance.androidclient.viewmodels.UpcomingMatchNotificationViewModel;
import com.thebluealliance.androidclient.views.NoDataView;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Named;

import io.nlopez.smartadapters.SmartAdapter;
import io.nlopez.smartadapters.adapters.RecyclerMultiAdapter;
import io.nlopez.smartadapters.utils.Mapper;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public abstract class FirebaseTickerFragment extends Fragment implements Action1<List<FirebaseNotification>>, View.OnClickListener {

    public static final String FIREBASE_URL_DEFAULT = "https://thebluealliance.firebaseio.com/";
    public static final int FIREBASE_LOAD_DEPTH_DEFAULT = 25;

    private enum FirebaseChildNodesState {
        LOADING,
        HAS_CHILDREN,
        NO_CHILDREN
    }

    @Inject DatabaseWriter mWriter;
    @Inject @Named("firebase_api") FirebaseAPI mFirebaseApi;

    private RecyclerView mNotificationsRecyclerView;
    private ListView mFilterListView;
    private NoDataView mNoDataView;
    private ProgressBar mProgressBar;
    private AnimatedRecyclerMultiAdapter mNotificationsAdapter;
    private ListViewAdapter mNotificationFilterAdapter;
    private List<BaseNotification> mAllNotifications = new ArrayList<>();
    private boolean mAreFilteredNotificationsVisible = false;
    private ResumeableRxFirebase mFirebaseSubscriber;
    private LinearLayoutManager mLayoutManager;

    private Parcelable mListState;

    private TextView leftButton, rightButton;
    private Set<String> enabledNotifications;
    private boolean filterListShowing;

    private FirebaseChildNodesState mChildNodeState = FirebaseChildNodesState.LOADING;
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
                .onBackpressureBuffer()
                .filter(childEvent -> childEvent != null && childEvent.eventType == FirebaseChildType.CHILD_ADDED)
                .map(childEvent1 -> childEvent1.snapshot.getValue(FirebaseNotification.class))
                .buffer(5, TimeUnit.SECONDS, 5)
                .filter(itemList -> itemList != null && !itemList.isEmpty())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this, throwable -> {
                    Log.e(Constants.LOG_TAG, "Firebase error: " + throwable);
                    throwable.printStackTrace();
                    // Show the "none found" warning
                    mProgressBar.setVisibility(View.GONE);
                    mNotificationsRecyclerView.setVisibility(View.GONE);
                    mNoDataView.setVisibility(View.VISIBLE);
                    mNoDataView.setText(R.string.firebase_no_matching_items);
                });

        Firebase.setAndroidContext(getActivity());
        Firebase ticker = new Firebase(mFirebaseUrl);
        ticker.orderByKey().limitToLast(mFirebaseLoadDepth).addChildEventListener(mFirebaseSubscriber);

        Observable<JsonElement> oneItem = mFirebaseApi.getOneItemFromNode(getFirebaseUrlSuffix());
        if (oneItem != null) {
            oneItem.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(result -> {
                        if (result == null || result.isJsonNull()) {
                            mChildNodeState = FirebaseChildNodesState.NO_CHILDREN;
                        } else if (result.isJsonObject()) {
                            if (((JsonObject) result).entrySet().size() > 0) {
                                mChildNodeState = FirebaseChildNodesState.HAS_CHILDREN;
                            } else {
                                mChildNodeState = FirebaseChildNodesState.NO_CHILDREN;
                            }
                        }
                        updateViewVisibility();
                    }, throwable -> {
                        Log.e(Constants.LOG_TAG, "Firebase rest error: " + throwable);
                        throwable.printStackTrace();

                        // net error getting item count, show no data view
                        mChildNodeState = FirebaseChildNodesState.NO_CHILDREN;
                        updateViewVisibility();
                    });
        }

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

        mNotificationsRecyclerView = (RecyclerView) v.findViewById(R.id.list);
        mNotificationsRecyclerView.setHasFixedSize(true);
        mNotificationsRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mLayoutManager = new LinearLayoutManager(getContext());
        mNotificationsRecyclerView.setLayoutManager(mLayoutManager);
        mProgressBar = (ProgressBar) v.findViewById(R.id.progress);
        updateViewVisibility();

        if (mNotificationsAdapter != null) {
            mNotificationsRecyclerView.setAdapter(mNotificationsAdapter);
            mLayoutManager.onRestoreInstanceState(mListState);
            Log.d("onCreateView", "using existing adapter");
        } else {
            mNotificationsAdapter = new AnimatedRecyclerMultiAdapter(createAdapterMapper(), new ArrayList<>());
            mNotificationsRecyclerView.setAdapter(mNotificationsAdapter);
        }

        setUpFilterListViews();

        return v;
    }

    private void updateViewVisibility() {
        if (mAllNotifications.size() > 0) {
            if (mAreFilteredNotificationsVisible) {
                // We've received at least one notification from the client library, and it's
                // visible with the current applied filter. Show the list!
                mNotificationsRecyclerView.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.GONE);
                mNoDataView.setVisibility(View.GONE);
            } else {
                // We've received at least one notification from the client library, but no
                // notification are visible with the current applied filter. Show the no data view,
                // but with a special "none found with that filter" message
                mNotificationsRecyclerView.setVisibility(View.GONE);
                mProgressBar.setVisibility(View.GONE);
                mNoDataView.setVisibility(View.VISIBLE);
                mNoDataView.setText(R.string.firebase_no_matching_items);
            }
        } else if (mChildNodeState == FirebaseChildNodesState.NO_CHILDREN) {
            // We've received the result of the REST call to the server and the list is (at least
            // for now) definitely empty. Show the no data view
            mNotificationsRecyclerView.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.GONE);
            mNoDataView.setVisibility(View.VISIBLE);
            mNoDataView.setText(R.string.firebase_empty_ticker);
        } else {
            // We haven't yet received any notifications from the client library, but we also
            // aren't certain that the list is empty. Show the spinner.
            mNotificationsRecyclerView.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.VISIBLE);
            mNoDataView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mFirebaseSubscriber.resumeDelivery();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mNotificationsRecyclerView != null) {
            Log.d("onPause", "saving adapter");
            mNotificationsAdapter = (AnimatedRecyclerMultiAdapter) mNotificationsRecyclerView.getAdapter();
            mListState = mLayoutManager.onSaveInstanceState();
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
            mFilterListView.setVisibility(View.VISIBLE);
            leftButton.setVisibility(View.VISIBLE);
            leftButton.setText(R.string.firebase_cancel);
            rightButton.setText(R.string.firebase_apply_filter);
        } else {
            mFilterListView.setVisibility(View.GONE);
            leftButton.setVisibility(View.GONE);
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
                .filter(n -> n != null)
                .map(notification -> {
                    notification.parseMessageData();
                    return notification.renderToViewModel(getContext(), null);
                })
                .toList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(notificationsList -> {
                    mAreFilteredNotificationsVisible = !notificationsList.isEmpty();

                    if (!FirebaseTickerFragment.this.isResumed() || getView() == null) {
                        return;
                    }

                    mNotificationsAdapter.animateTo(notificationsList);

                    // If we're at the top of the list, maintain our position so any new items
                    // above our current first item will animate into view
                    if (mNotificationsRecyclerView.computeVerticalScrollOffset() == 0) {
                        mNotificationsRecyclerView.scrollToPosition(0);
                    }

                    updateViewVisibility();
                }, throwable -> {
                    Log.e(Constants.LOG_TAG, "Firebase error");
                    throwable.printStackTrace();
                    // Show the "none found" warning
                    mAreFilteredNotificationsVisible = false;
                    updateViewVisibility();
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
        if (firebaseNotifications.isEmpty()) {
            return;
        }

        for (FirebaseNotification firebaseNotification : firebaseNotifications) {
            mComponent.inject(firebaseNotification);
            mAllNotifications.add(0, firebaseNotification.getNotification());
        }

        updateList();
    }

    public Mapper createAdapterMapper() {
        Mapper mapper = new Mapper();
        mapper.add(AllianceSelectionNotificationViewModel.class, AllianceSelectionNotificationItemView.class)
                .add(AwardsPostedNotificationViewModel.class, AwardsPostedNotificationItemView.class)
                .add(CompLevelStartingNotificationViewModel.class, CompLevelStartingNotificationItemView.class)
                .add(UpcomingMatchNotificationViewModel.class, UpcomingMatchNotificationItemView.class)
                .add(ScoreNotificationViewModel.class, ScoreNotificationItemView.class)
                .add(ScheduleUpdatedNotificationViewModel.class, ScheduleUpdatedNotificationItemView.class)
                .add(GenericNotificationViewModel.class, GenericNotificationItemView.class);
        return mapper;
    }

}
