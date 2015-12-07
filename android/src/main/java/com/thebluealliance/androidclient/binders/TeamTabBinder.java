package com.thebluealliance.androidclient.binders;

import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;

import com.thebluealliance.androidclient.adapters.TeamListFragmentPagerAdapter;
import com.thebluealliance.androidclient.views.SlidingTabs;

import javax.inject.Inject;

import butterknife.ButterKnife;

public class TeamTabBinder extends AbstractDataBinder<Integer> {

    public ViewPager viewPager;
    public SlidingTabs tabs;
    public FragmentManager fragmentManager;

    @Inject
    public TeamTabBinder() {
        super();
    }

    @Override
    public void updateData(@Nullable Integer data) {
        /**
         * Fix for really strange bug. Menu bar items wouldn't appear only when navigated to from 'Events' in the nav drawer
         * Bug is some derivation of this: https://code.google.com/p/android/issues/detail?id=29472
         * So set the view pager's adapter in another thread to avoid a race condition, or something.
         */
        viewPager.post(() -> {
            viewPager.setAdapter(new TeamListFragmentPagerAdapter(fragmentManager, data == null ? 0 : data));
            tabs.setViewPager(viewPager);
        });
    }

    @Override
    public void onComplete() {

    }

    @Override
    public void bindViews() {
        ButterKnife.bind(this, mRootView);
    }

    @Override
    public void unbind() {
        super.unbind();
        ButterKnife.unbind(this);
    }

    @Override
    public void onError(Throwable throwable) {

    }
}
