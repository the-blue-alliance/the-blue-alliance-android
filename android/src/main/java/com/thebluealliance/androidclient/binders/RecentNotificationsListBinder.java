package com.thebluealliance.androidclient.binders;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;

import androidx.recyclerview.widget.RecyclerView;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.TbaLogger;
import com.thebluealliance.androidclient.databinding.FragmentRecentNotificationsBinding;
import com.thebluealliance.androidclient.eventbus.NotificationsUpdatedEvent;
import com.thebluealliance.androidclient.gcm.notifications.BaseNotification;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

public class RecentNotificationsListBinder extends AbstractRecyclerViewBinder<FragmentRecentNotificationsBinding> {

    private int mNewNotificationCount = 0;
    private boolean mIsNotificationIndicatorVisible = true;

    @Override
    protected RecyclerView getList() {
        return mBinding.list;
    }

    @Override
    protected ProgressBar getProgress() {
        return mBinding.progress;
    }

    @Override
    public void bindViews() {
        mBinding = FragmentRecentNotificationsBinding.bind(mRootView);

        mBinding.newNotificationIndicator.setOnClickListener(v -> {
            hideNewNotificationIndicator(true);
            // Count should be reset after we start the hide animation
            mNewNotificationCount = 0;
            mBinding.list.smoothScrollToPosition(0);
        });

        hideNewNotificationIndicator(false);

        mBinding.list.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                // If we're at the top of the list, hide the notif indicator and reset the new
                // notif count
                if (mBinding.list.computeVerticalScrollOffset() == 0) {
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
        mBinding.newNotificationIndicator.setText(mActivity.getString(R.string.new_notifications, mNewNotificationCount));
    }

    private void showNewNotificationIndicator(boolean animate) {
        if (mIsNotificationIndicatorVisible || mNewNotificationCount == 0) {
            return;
        }
        mIsNotificationIndicatorVisible = true;


        // Defaults to invisible in the layout file so it doesn't show while the activity is
        // launching; this will only have an effect the first time it's called
        mBinding.newNotificationIndicator.setVisibility(View.VISIBLE);

        if (animate) {
            if (mBinding.newNotificationIndicator.getAnimation() != null) {
                mBinding.newNotificationIndicator.getAnimation().cancel();
            }

            Animation anim = AnimationUtils.loadAnimation(mActivity, R.anim.slide_down);
            anim.setAnimationListener(new AnimationListenerAdapter() {
                @Override
                public void onAnimationEnd(Animation animation) {
                    mBinding.newNotificationIndicator.setTranslationY(0.0f);
                }
            });

            mBinding.newNotificationIndicator.setTranslationY(0.0f);
            mBinding.newNotificationIndicator.startAnimation(anim);
        } else {
            mBinding.newNotificationIndicator.setTranslationY(0);
        }
    }

    private void hideNewNotificationIndicator(boolean animate) {
        if (!mIsNotificationIndicatorVisible) {
            return;
        }
        mIsNotificationIndicatorVisible = false;
        if (animate && mNewNotificationCount != 0) {
            if (mBinding.newNotificationIndicator.getAnimation() != null) {
                mBinding.newNotificationIndicator.getAnimation().cancel();
            }

            Animation anim = AnimationUtils.loadAnimation(mActivity, R.anim.slide_up);
            anim.setAnimationListener(new AnimationListenerAdapter() {
                @Override
                public void onAnimationEnd(Animation animation) {
                    mBinding.newNotificationIndicator.setTranslationY(-1.5f * mBinding.newNotificationIndicator.getHeight());
                }
            });

            mBinding.newNotificationIndicator.setTranslationY(0.0f);
            mBinding.newNotificationIndicator.startAnimation(anim);
        } else {
            mBinding.newNotificationIndicator.setTranslationY(-1.5f * mBinding.newNotificationIndicator.getHeight());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNotificationReceived(NotificationsUpdatedEvent event) {
        BaseNotification notification = event.getNotification();
        notification.parseMessageData();
        addItemToBeginningOfList(notification.renderToViewModel(mActivity, null));
        if (mBinding.list.computeVerticalScrollOffset() == 0) {
            mNewNotificationCount = 0;
            mBinding.list.scrollToPosition(0);
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

        mBinding.progress.setVisibility(View.GONE);
        mBinding.list.setVisibility(View.VISIBLE);
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
