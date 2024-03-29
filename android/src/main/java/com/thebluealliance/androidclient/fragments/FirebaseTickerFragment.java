package com.thebluealliance.androidclient.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.firebase.client.Firebase;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.TbaLogger;
import com.thebluealliance.androidclient.ViewUtilities;
import com.thebluealliance.androidclient.adapters.AnimatedRecyclerMultiAdapter;
import com.thebluealliance.androidclient.adapters.ListViewAdapter;
import com.thebluealliance.androidclient.database.DatabaseWriter;
import com.thebluealliance.androidclient.databinding.FragmentFirebaseTickerBinding;
import com.thebluealliance.androidclient.datafeed.retrofit.FirebaseAPI;
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Named;

import dagger.hilt.android.AndroidEntryPoint;
import io.nlopez.smartadapters.utils.Mapper;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

@AndroidEntryPoint
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

    private FragmentFirebaseTickerBinding mBinding;

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
                    mBinding.progress.setVisibility(View.GONE);
                    mBinding.list.setVisibility(View.GONE);
                    mBinding.noData.setVisibility(View.VISIBLE);
                    mBinding.noData.setText(R.string.firebase_no_matching_items);
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
        mBinding = FragmentFirebaseTickerBinding.inflate(inflater, container, false);
        View v = mBinding.getRoot();

        mBinding.noData.setImage(R.drawable.ic_notifications_black_48dp);

        if (mNotificationFilterAdapter == null) {
            mNotificationFilterAdapter = createFilterListAdapter();
        }
        mBinding.filterList.setAdapter(mNotificationFilterAdapter);

        mBinding.leftButton.setOnClickListener(this);
        mBinding.rightButton.setOnClickListener(this);

        mBinding.list.setHasFixedSize(true);
        mBinding.list.setItemAnimator(new DefaultItemAnimator());
        mLayoutManager = new LinearLayoutManager(getContext());
        mBinding.list.setLayoutManager(mLayoutManager);

        if (mNotificationsAdapter != null) {
            mBinding.list.setAdapter(mNotificationsAdapter);
            mLayoutManager.onRestoreInstanceState(mListState);
            TbaLogger.d("onCreateView: using existing adapter");
        } else {
            mNotificationsAdapter = new AnimatedRecyclerMultiAdapter(createAdapterMapper(), new ArrayList<>());
            mBinding.list.setAdapter(mNotificationsAdapter);
        }

        updateViewVisibility();
        // Do this after layout so that the filter container will have a defined height
        ViewUtilities.runOnceAfterLayout(mBinding.filterListContainer, () -> hideFilter(false, null));

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
                        .fadeIn(mBinding.list)
                        .fadeOut(mBinding.progress)
                        .fadeOut(mBinding.noData)
                        .start();
            } else {
                // We've received at least one notification from the client library, but no
                // notification are visible with the current applied filter. Show the no data view,
                // but with a special "none found with that filter" message
                mBinding.noData.setText(R.string.firebase_no_matching_items);
                ViewCrossfader.create(ANIMATION_DURATION)
                        .fadeOut(mBinding.list)
                        .fadeOut(mBinding.progress)
                        .fadeIn(mBinding.noData)
                        .start();
            }
        } else if (mChildNodeState == FirebaseChildNodesState.NO_CHILDREN) {
            // We've received the result of the REST call to the server and the list is (at least
            // for now) definitely empty. Show the no data view
            mBinding.noData.setText(R.string.firebase_empty_ticker);
            ViewCrossfader.create(ANIMATION_DURATION)
                    .fadeOut(mBinding.list)
                    .fadeOut(mBinding.progress)
                    .fadeIn(mBinding.noData)
                    .start();
        } else {
            // We haven't yet received any notifications from the client library, but we also
            // aren't certain that the list is empty. Show the spinner.
            ViewCrossfader.create(ANIMATION_DURATION)
                    .fadeOut(mBinding.list)
                    .fadeIn(mBinding.progress)
                    .fadeOut(mBinding.noData)
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
        if (mBinding != null) {
            TbaLogger.d("onPause: saving adapter");
            mNotificationsAdapter = (AnimatedRecyclerMultiAdapter) mBinding.list.getAdapter();
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
                mBinding.filterList.setAdapter(mNotificationFilterAdapter);
            });
        }
    }

    private void showFilter(boolean animate) {
        filterListShowing = true;

        mBinding.filterList.setVisibility(View.VISIBLE);
        mBinding.leftButton.setVisibility(View.VISIBLE);
        mBinding.leftButton.setText(R.string.firebase_cancel);
        mBinding.rightButton.setText(R.string.firebase_apply_filter);
        mBinding.filterShadow.setVisibility(View.GONE);

        if (animate) {
            mBinding.filterList.animate()
                    .alpha(1.0f)
                    .setDuration(ANIMATION_DURATION)
                    .setStartDelay(ANIMATION_DURATION)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override public void onAnimationEnd(Animator animation) {
                            mBinding.filterList.setAlpha(1.0f);
                        }
                    }).start();

            mBinding.filterListContainer.animate()
                    .translationY(0)
                    .setDuration(ANIMATION_DURATION)
                    .setStartDelay(0)
                    .setInterpolator(new AccelerateDecelerateInterpolator())
                    .setListener(new AnimatorListenerAdapter() {
                        @Override public void onAnimationEnd(Animator animation) {
                            mBinding.filterListContainer.setTranslationY(0);
                        }
                    }).start();

            mBinding.foregroundDim.animate()
                    .alpha(DIMMED_ALPHA)
                    .setDuration(ANIMATION_DURATION)
                    .setStartDelay(0)
                    .setInterpolator(new AccelerateDecelerateInterpolator())
                    .setListener(new AnimatorListenerAdapter() {
                        @Override public void onAnimationEnd(Animator animation) {
                            mBinding.foregroundDim.setAlpha(DIMMED_ALPHA);
                        }
                    }).start();
        } else {
            mBinding.filterListContainer.setTranslationY(0);
            mBinding.foregroundDim.setAlpha(DIMMED_ALPHA);
            mBinding.filterList.setAlpha(1.0f);
        }
    }

    private void hideFilter(boolean animate, Runnable onHidden) {
        filterListShowing = false;

        mBinding.leftButton.setVisibility(View.GONE);
        mBinding.rightButton.setText(R.string.firebase_filter);

        int viewHeight = getView().getHeight();

        if (animate) {
            mBinding.filterList.animate()
                    .alpha(0.0f)
                    .setDuration(ANIMATION_DURATION)
                    .setStartDelay(0)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override public void onAnimationEnd(Animator animation) {
                            mBinding.filterList.setAlpha(0.0f);
                        }
                    }).start();

            mBinding.filterListContainer.animate()
                    .translationY(viewHeight)
                    .setDuration(ANIMATION_DURATION)
                    .setStartDelay(ANIMATION_DURATION)
                    .setInterpolator(new AccelerateDecelerateInterpolator())
                    .setListener(new AnimatorListenerAdapter() {
                        @Override public void onAnimationEnd(Animator animation) {
                            mBinding.filterListContainer.setTranslationY(viewHeight);
                            mBinding.filterListContainer.setVisibility(View.GONE);
                            mBinding.filterShadow.setVisibility(View.VISIBLE);
                            if (onHidden != null) {
                                onHidden.run();
                            }
                        }
                    }).start();

            mBinding.foregroundDim.animate()
                    .alpha(0.0f)
                    .setDuration(ANIMATION_DURATION)
                    .setStartDelay(ANIMATION_DURATION)
                    .setInterpolator(new AccelerateDecelerateInterpolator())
                    .setListener(new AnimatorListenerAdapter() {
                        @Override public void onAnimationEnd(Animator animation) {
                            mBinding.foregroundDim.setAlpha(0.0f);
                        }
                    }).start();
        } else {
            mBinding.filterListContainer.setTranslationY(mBinding.filterListContainer.getHeight());
            mBinding.filterList.setAlpha(0.0f);
            mBinding.filterShadow.setVisibility(View.VISIBLE);
            mBinding.foregroundDim.setAlpha(0.0f);
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
        //FIXME read these from FirebaseRemoteConfig
        /*
        String firebaseBase = Utilities.readLocalProperty(getActivity(), "firebase.url",
                                                           FIREBASE_URL_DEFAULT);
        mFirebaseUrl = firebaseBase + getFirebaseUrlSuffix();
        String loadDepthTemp = Utilities.readLocalProperty(getActivity(), "firebase.depth",
                                                            Integer.toString(FIREBASE_LOAD_DEPTH_DEFAULT));

        try {
            mFirebaseLoadDepth = Integer.parseInt(loadDepthTemp);
        } catch (NumberFormatException e) {
            mFirebaseLoadDepth = FIREBASE_LOAD_DEPTH_DEFAULT;
        }*/
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
                    if (mBinding.list.computeVerticalScrollOffset() == 0) {
                        mBinding.list.scrollToPosition(0);
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
        int filterItemCount = mBinding.filterList.getAdapter().getCount();
        for (int i = 0; i < filterItemCount; i++) {
            GamedayTickerFilterCheckbox checkbox = ((GamedayTickerFilterCheckbox) mBinding.filterList.getAdapter().getItem(i));
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
