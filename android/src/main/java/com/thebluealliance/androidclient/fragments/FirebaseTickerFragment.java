package com.thebluealliance.androidclient.fragments;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import com.firebase.client.Firebase;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.TbaLogger;
import com.thebluealliance.androidclient.Utilities;
import com.thebluealliance.androidclient.ViewUtilities;
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

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
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

import butterknife.Bind;
import butterknife.ButterKnife;
import io.nlopez.smartadapters.utils.Mapper;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public abstract class FirebaseTickerFragment extends Fragment implements Action1<List<FirebaseNotification>>, View.OnClickListener {

    public static final String FIREBASE_URL_DEFAULT = "https://thebluealliance.firebaseio.com/";
    public static final int FIREBASE_LOAD_DEPTH_DEFAULT = 25;

    private static final int ANIMATION_DURATION = 300;
    private static final float DIMMED_ALPHA = 0.6f;

    private enum FirebaseChildNodesState {
        LOADING,
        HAS_CHILDREN,
        NO_CHILDREN
    }

    @Inject DatabaseWriter mWriter;
    @Inject @Named("firebase_api") FirebaseAPI mFirebaseApi;

    @Bind(R.id.list) RecyclerView mNotificationsRecyclerView;
    @Bind(R.id.filter_list) ListView mFilterListView;
    @Bind(R.id.filter_list_container) View mFilterListContainer;
    @Bind(R.id.no_data) NoDataView mNoDataView;
    @Bind(R.id.progress) ProgressBar mProgressBar;
    @Bind(R.id.left_button) TextView mLeftButton;
    @Bind(R.id.right_button) TextView mRightButton;
    @Bind(R.id.filter_shadow) View mShadow;
    @Bind(R.id.foreground_dim) View mForegroundDim;

    private AnimatedRecyclerMultiAdapter mNotificationsAdapter;
    private ListViewAdapter mNotificationFilterAdapter;
    private List<BaseNotification> mAllNotifications = new ArrayList<>();
    private boolean mAreFilteredNotificationsVisible = false;
    private ResumeableRxFirebase mFirebaseSubscriber;
    private LinearLayoutManager mLayoutManager;
    private Parcelable mListState;
    private Set<String> enabledNotifications;
    private boolean filterListShowing;
    private FirebaseChildNodesState mChildNodeState = FirebaseChildNodesState.LOADING;
    private String mFirebaseUrl;
    private int mFirebaseLoadDepth;
    protected FragmentComponent mComponent;

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
                    TbaLogger.e("Firebase error: " + throwable);
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
                        TbaLogger.e("Firebase rest error: " + throwable);
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

        ButterKnife.bind(this, v);

        mNoDataView.setImage(R.drawable.ic_notifications_black_48dp);

        if (mNotificationFilterAdapter == null) {
            mNotificationFilterAdapter = createFilterListAdapter();
        }
        mFilterListView.setAdapter(mNotificationFilterAdapter);

        mLeftButton.setOnClickListener(this);
        mRightButton.setOnClickListener(this);

        mNotificationsRecyclerView.setHasFixedSize(true);
        mNotificationsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mLayoutManager = new LinearLayoutManager(getContext());
        mNotificationsRecyclerView.setLayoutManager(mLayoutManager);

        if (mNotificationsAdapter != null) {
            mNotificationsRecyclerView.setAdapter(mNotificationsAdapter);
            mLayoutManager.onRestoreInstanceState(mListState);
            TbaLogger.d("onCreateView: using existing adapter");
        } else {
            mNotificationsAdapter = new AnimatedRecyclerMultiAdapter(createAdapterMapper(), new ArrayList<>());
            mNotificationsRecyclerView.setAdapter(mNotificationsAdapter);
        }

        updateViewVisibility();
        // Do this after layout so that the filter container will have a defined height
        ViewUtilities.runOnceAfterLayout(mFilterListContainer, () -> hideFilter(false, null));

        return v;
    }

    @Override public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        hideFilter(false, null);
    }

    private void updateViewVisibility() {
        if (mAllNotifications.size() > 0) {
            if (mAreFilteredNotificationsVisible) {
                // We've received at least one notification from the client library, and it's
                // visible with the current applied filter. Show the list!
                ViewCrossfader.create(ANIMATION_DURATION)
                        .fadeIn(mNotificationsRecyclerView)
                        .fadeOut(mProgressBar)
                        .fadeOut(mNoDataView)
                        .start();
            } else {
                // We've received at least one notification from the client library, but no
                // notification are visible with the current applied filter. Show the no data view,
                // but with a special "none found with that filter" message
                mNoDataView.setText(R.string.firebase_no_matching_items);
                ViewCrossfader.create(ANIMATION_DURATION)
                        .fadeOut(mNotificationsRecyclerView)
                        .fadeOut(mProgressBar)
                        .fadeIn(mNoDataView)
                        .start();
            }
        } else if (mChildNodeState == FirebaseChildNodesState.NO_CHILDREN) {
            // We've received the result of the REST call to the server and the list is (at least
            // for now) definitely empty. Show the no data view
            mNoDataView.setText(R.string.firebase_empty_ticker);
            ViewCrossfader.create(ANIMATION_DURATION)
                    .fadeOut(mNotificationsRecyclerView)
                    .fadeOut(mProgressBar)
                    .fadeIn(mNoDataView)
                    .start();
        } else {
            // We haven't yet received any notifications from the client library, but we also
            // aren't certain that the list is empty. Show the spinner.
            ViewCrossfader.create(ANIMATION_DURATION)
                    .fadeOut(mNotificationsRecyclerView)
                    .fadeIn(mProgressBar)
                    .fadeOut(mNoDataView)
                    .start();
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
            TbaLogger.d("onPause: saving adapter");
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
                showFilter(true);
                enabledNotifications = getEnabledNotificationKeys();
            } else {
                hideFilter(true, this::updateList);
            }
        } else if (id == R.id.left_button) {
            // Don't reset the adapter until the list is closed
            // This prevents the checkboxes from briefly flashing to their default state before
            // the list is hidden
            hideFilter(true, () -> {
                mNotificationFilterAdapter = createFilterListAdapter(enabledNotifications);
                mFilterListView.setAdapter(mNotificationFilterAdapter);
            });
        }
    }

    private void showFilter(boolean animate) {
        filterListShowing = true;

        mFilterListContainer.setVisibility(View.VISIBLE);
        mLeftButton.setVisibility(View.VISIBLE);
        mLeftButton.setText(R.string.firebase_cancel);
        mRightButton.setText(R.string.firebase_apply_filter);
        mShadow.setVisibility(View.GONE);

        if (animate) {
            mFilterListView.animate()
                    .alpha(1.0f)
                    .setDuration(ANIMATION_DURATION)
                    .setStartDelay(ANIMATION_DURATION)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override public void onAnimationEnd(Animator animation) {
                            mFilterListView.setAlpha(1.0f);
                        }
                    }).start();

            mFilterListContainer.animate()
                    .translationY(0)
                    .setDuration(ANIMATION_DURATION)
                    .setStartDelay(0)
                    .setInterpolator(new AccelerateDecelerateInterpolator())
                    .setListener(new AnimatorListenerAdapter() {
                        @Override public void onAnimationEnd(Animator animation) {
                            mFilterListContainer.setTranslationY(0);
                        }
                    }).start();

            mForegroundDim.animate()
                    .alpha(DIMMED_ALPHA)
                    .setDuration(ANIMATION_DURATION)
                    .setStartDelay(0)
                    .setInterpolator(new AccelerateDecelerateInterpolator())
                    .setListener(new AnimatorListenerAdapter() {
                        @Override public void onAnimationEnd(Animator animation) {
                            mForegroundDim.setAlpha(DIMMED_ALPHA);
                        }
                    }).start();
        } else {
            mFilterListContainer.setTranslationY(0);
            mForegroundDim.setAlpha(DIMMED_ALPHA);
            mFilterListView.setAlpha(1.0f);
        }
    }

    private void hideFilter(boolean animate, Runnable onHidden) {
        filterListShowing = false;

        mLeftButton.setVisibility(View.GONE);
        mRightButton.setText(R.string.firebase_filter);

        int viewHeight = getView().getHeight();

        if (animate) {
            mFilterListView.animate()
                    .alpha(0.0f)
                    .setDuration(ANIMATION_DURATION)
                    .setStartDelay(0)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override public void onAnimationEnd(Animator animation) {
                            mFilterListView.setAlpha(0.0f);
                        }
                    }).start();

            mFilterListContainer.animate()
                    .translationY(viewHeight)
                    .setDuration(ANIMATION_DURATION)
                    .setStartDelay(ANIMATION_DURATION)
                    .setInterpolator(new AccelerateDecelerateInterpolator())
                    .setListener(new AnimatorListenerAdapter() {
                        @Override public void onAnimationEnd(Animator animation) {
                            mFilterListContainer.setTranslationY(viewHeight);
                            mFilterListContainer.setVisibility(View.GONE);
                            mShadow.setVisibility(View.VISIBLE);
                            if (onHidden != null) {
                                onHidden.run();
                            }
                        }
                    }).start();

            mForegroundDim.animate()
                    .alpha(0.0f)
                    .setDuration(ANIMATION_DURATION)
                    .setStartDelay(ANIMATION_DURATION)
                    .setInterpolator(new AccelerateDecelerateInterpolator())
                    .setListener(new AnimatorListenerAdapter() {
                        @Override public void onAnimationEnd(Animator animation) {
                            mForegroundDim.setAlpha(0.0f);
                        }
                    }).start();
        } else {
            mFilterListContainer.setTranslationY(mFilterListContainer.getHeight());
            mFilterListView.setAlpha(0.0f);
            mShadow.setVisibility(View.VISIBLE);
            mForegroundDim.setAlpha(0.0f);
            if (onHidden != null) {
                onHidden.run();
            }
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
                .filter(n -> n != null)
                .filter(notification -> enabledNotificationKeys.contains(notification.getNotificationType()))
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
                    TbaLogger.e("Firebase error");
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

    protected abstract void inject();

    protected abstract String getFirebaseUrlSuffix();

    private static final class ViewCrossfader {
        private List<View> mFadeIn, mFadeOut;
        private int mDuration;

        private ViewCrossfader(int duration) {
            mDuration = duration;
            mFadeIn = new ArrayList<>();
            mFadeOut = new ArrayList<>();
        }

        public static ViewCrossfader create(int duration) {
            return new ViewCrossfader(duration);
        }

        public ViewCrossfader fadeIn(View v) {
            mFadeIn.add(v);
            return this;
        }

        public ViewCrossfader fadeOut(View v) {
            mFadeOut.add(v);
            return this;
        }

        public void start() {
            for (View fadeOut : mFadeOut) {
                fadeOut.animate()
                        .alpha(0.0f)
                        .setDuration(mDuration)
                        .setStartDelay(0)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override public void onAnimationEnd(Animator animation) {
                                fadeOut.setVisibility(View.GONE);
                            }
                        }).start();
            }

            for (View fadeIn : mFadeIn) {
                fadeIn.animate()
                        .alpha(1.0f)
                        .setDuration(mDuration)
                        .setStartDelay(mDuration)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override public void onAnimationStart(Animator animation) {
                                fadeIn.setVisibility(View.VISIBLE);
                            }
                        }).start();
            }
        }
    }
}
