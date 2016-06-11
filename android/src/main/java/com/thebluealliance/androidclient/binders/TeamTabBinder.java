package com.thebluealliance.androidclient.binders;

import com.thebluealliance.androidclient.adapters.TeamListFragmentPagerAdapter;
import com.thebluealliance.androidclient.views.SlidingTabs;

import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;

import javax.inject.Inject;

import butterknife.ButterKnife;

public class TeamTabBinder extends AbstractDataBinder<Integer> {

    public ViewPager viewPager;
    public SlidingTabs tabs;
    public FragmentManager fragmentManager;

    private Integer oldData;

    private int mInitialTab;

    @Inject
    public TeamTabBinder() {
        super();
        mInitialTab = 0;
    }

    public void setInitialTab(int initialTab) {
        mInitialTab = initialTab;
    }

    @Override
    public void updateData(@Nullable Integer data) {
        if (data != null && oldData != null && data.equals(oldData)) {
            // No need to update anything
            return;
        }
        /**
         * Fix for really strange bug. Menu bar items wouldn't appear only when navigated to from 'Events' in the nav drawer
         * Bug is some derivation of this: https://code.google.com/p/android/issues/detail?id=29472
         * So set the view pager's adapter in another thread to avoid a race condition, or something.
         */
        viewPager.post(() -> {
            viewPager.setAdapter(new TeamListFragmentPagerAdapter(fragmentManager, data == null ? 0 : data));
            tabs.setViewPager(viewPager);
            viewPager.setCurrentItem(mInitialTab);
        });

        oldData = data;
    }

    @Override
    public void onComplete() {

    }

    @Override
    public void bindViews() {
        ButterKnife.bind(this, mRootView);
    }

    @Override
    public void unbind(boolean unbindViews) {
        super.unbind(unbindViews);
        if (unbindViews) {
            ButterKnife.unbind(this);
        }
    }

    @Override
    public void onError(Throwable throwable) {

    }
}
