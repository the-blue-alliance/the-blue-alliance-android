package com.thebluealliance.androidclient.binders;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.eventbus.NotificationsUpdatedEvent;
import com.thebluealliance.androidclient.gcm.notifications.BaseNotification;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import java.util.ArrayList;

public class RecentNotificationsListBinder extends RecyclerViewBinder {

    private int mNewNotificationCount = 0;
    private TextView mNewNotificationIndicator;
    private boolean mIsNotificationIndicatorVisible = true;

    @Override
    public void bindViews() {
        super.bindViews();

        mNewNotificationIndicator = (TextView) mRootView.findViewById(R.id.new_notification_indicator);
        mNewNotificationIndicator.setOnClickListener(v -> {
            hideNewNotificationIndicator(true);
            // Count should be reset after we start the hide animation
            mNewNotificationCount = 0;
            mRecyclerView.smoothScrollToPosition(0);
        });

        hideNewNotificationIndicator(false);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                // If we're at the top of the list, hide the notif indicator and reset the new
                // notif count
                if (mRecyclerView.computeVerticalScrollOffset() == 0) {
                    hideNewNotificationIndicator(true);
                    // Count should be reset after we start the hide animation
                    mNewNotificationCount = 0;
                    updateNewNotificationIndicator();
                    return;
                }

                if (dy > 0) {
                    // We scrolled down in the list; hide the indicator
                    hideNewNotificationIndicator(true);
                } else if (dy < 0) {
                    // We scrolled up in the list
                    showNewNotificationIndicator(true);
                }
            }
        });
    }

    private void updateNewNotificationIndicator() {
        mNewNotificationIndicator.setText(mActivity.getString(R.string.new_notifications, mNewNotificationCount));
    }

    private void showNewNotificationIndicator(boolean animate) {
        Log.d(Constants.LOG_TAG, "show notification!");
        if (mIsNotificationIndicatorVisible || mNewNotificationCount == 0) {
            return;
        }
        mIsNotificationIndicatorVisible = true;
        if (animate) {
            if (mNewNotificationIndicator.getAnimation() != null) {
                mNewNotificationIndicator.getAnimation().cancel();
            }

            Animation anim = AnimationUtils.loadAnimation(mActivity, R.anim.slide_down);
            anim.setAnimationListener(new AnimationListenerAdapter() {
                @Override
                public void onAnimationEnd(Animation animation) {
                    mNewNotificationIndicator.setTranslationY(0.0f);
                }
            });

            mNewNotificationIndicator.setTranslationY(0.0f);
            mNewNotificationIndicator.startAnimation(anim);
        } else {
            mNewNotificationIndicator.setTranslationY(0);
        }
    }

    private void hideNewNotificationIndicator(boolean animate) {
        Log.d(Constants.LOG_TAG, "hide notification!");
        if (!mIsNotificationIndicatorVisible) {
            return;
        }
        mIsNotificationIndicatorVisible = false;
        if (animate && mNewNotificationCount != 0) {
            if (mNewNotificationIndicator.getAnimation() != null) {
                mNewNotificationIndicator.getAnimation().cancel();
            }

            Animation anim = AnimationUtils.loadAnimation(mActivity, R.anim.slide_up);
            anim.setAnimationListener(new AnimationListenerAdapter() {
                @Override
                public void onAnimationEnd(Animation animation) {
                    mNewNotificationIndicator.setTranslationY(-1.5f * mNewNotificationIndicator.getHeight());
                }
            });

            mNewNotificationIndicator.setTranslationY(0.0f);
            mNewNotificationIndicator.startAnimation(anim);

            Log.d(Constants.LOG_TAG, "hide animation started");
        } else {
            mNewNotificationIndicator.setTranslationY(-1.5f * mNewNotificationIndicator.getHeight());
            Log.d(Constants.LOG_TAG, "hidden without animation");
        }
    }

    public void onEventMainThread(NotificationsUpdatedEvent event) {
        Log.d(Constants.LOG_TAG, "Updating notification list");
        BaseNotification notification = event.getNotification();
        notification.parseMessageData();
        if (notification.shouldShowInRecentNotificationsList()) {
            Log.d(Constants.LOG_TAG, "Adding notificatin to list");
            addItemToBeginningOfList(notification.renderToViewModel(mActivity, null));
            if (mRecyclerView.computeVerticalScrollOffset() == 0) {
                mNewNotificationCount = 0;
                mRecyclerView.scrollToPosition(0);
            } else {
                mNewNotificationCount++;
                updateNewNotificationIndicator();
                showNewNotificationIndicator(true);
            }
        }
    }


    private void addItemToBeginningOfList(Object item) {
        if (mList == null) {
            mList = new ArrayList<>();
        }

        if (mAdapter == null) {
            createAndInitializeAdapterForData(mList);
        }

        mList.add(0, item);

        mAdapter.setAutoDataSetChanged(false);
        mAdapter.setItems(mList);
        mAdapter.notifyItemInserted(0);
        mAdapter.setAutoDataSetChanged(true);

        if (mProgressBar != null) {
            mProgressBar.setVisibility(View.GONE);
        }

        if (mRecyclerView != null) {
            mRecyclerView.setVisibility(View.VISIBLE);
        }
        mNoDataBinder.unbindData();

        setDataBound(true);
    }

    private class AnimationListenerAdapter implements Animation.AnimationListener {

        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {

        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }

}
