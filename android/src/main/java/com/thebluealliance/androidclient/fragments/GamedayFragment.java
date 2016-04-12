package com.thebluealliance.androidclient.fragments;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.Utilities;
import com.thebluealliance.androidclient.adapters.GamedayFragmentPagerAdapter;
import com.thebluealliance.androidclient.views.SlidingTabs;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.Bind;
import butterknife.ButterKnife;

public class GamedayFragment extends Fragment {

    @Bind(R.id.pager) ViewPager mViewPager;
    @Bind(R.id.tabs) SlidingTabs mTabs;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_pager_with_tabs, container, false);
        ButterKnife.bind(this, v);

        // Make this ridiculously big
        mViewPager.setOffscreenPageLimit(50);
        mViewPager.setPageMargin(Utilities.getPixelsFromDp(getActivity(), 16));

        ViewCompat.setElevation(mTabs, getResources().getDimension(R.dimen.toolbar_elevation));

        /**
         * Fix for really strange bug. Menu bar items wouldn't appear only when navigated to from 'Events' in the nav drawer
         * Bug is some derivation of this: https://code.google.com/p/android/issues/detail?id=29472
         * So set the view pager's adapter in another thread to avoid a race condition, or something.
         */
        mViewPager.post(() -> {
            mViewPager.setAdapter(new GamedayFragmentPagerAdapter(getChildFragmentManager()));
            mTabs.setViewPager(mViewPager);
        });

        return v;
    }
}
