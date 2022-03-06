package com.thebluealliance.androidclient.binders;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.TbaLogger;
import com.thebluealliance.androidclient.eventbus.NotificationsUpdatedEvent;
import com.thebluealliance.androidclient.gcm.notifications.BaseNotification;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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
        if (mIsNotificationIndicatorVisible || mNewNotificationCount == 0) {
            return;
        }
        mIsNotificationIndicatorVisible = true;


        // Defaults to invisible in the layout file so it doesn't show while the activity is
        // launching; this will only have an effect the first time it's called
        mNewNotificationIndicator.setVisibility(View.VISIBLE);

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
        } else {
            mNewNotificationIndicator.setTranslationY(-1.5f * mNewNotificationIndicator.getHeight());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNotificationReceived(NotificationsUpdatedEvent event) {
        BaseNotification notification = event.getNotification();
        notification.parseMessageData();
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


    private void addItemToBeginningOfList(Object item) {
        if (item == null) {
            TbaLogger.w("Attempt to add a null ViewModel");
            return;
        }

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
