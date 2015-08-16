package com.thebluealliance.androidclient.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.Utilities;
import com.thebluealliance.androidclient.adapters.TeamListFragmentPagerAdapter;
import com.thebluealliance.androidclient.views.SlidingTabs;

public class AllTeamsListFragment extends Fragment {

    private ViewPager mViewPager;
    private SlidingTabs mTabs;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_team_list_fragment_pager, container, false);
        mViewPager = (ViewPager) v.findViewById(R.id.team_pager);
        // Make this ridiculously big
        mViewPager.setOffscreenPageLimit(50);
        mViewPager.setPageMargin(Utilities.getPixelsFromDp(getActivity(), 16));
        mTabs = (SlidingTabs) v.findViewById(R.id.team_pager_tabs);
        ViewCompat.setElevation(mTabs, getResources().getDimension(R.dimen.toolbar_elevation));

        /**
         * Fix for really strange bug. Menu bar items wouldn't appear only when navigated to from 'Events' in the nav drawer
         * Bug is some derivation of this: https://code.google.com/p/android/issues/detail?id=29472
         * So set the view pager's adapter in another thread to avoid a race condition, or something.
         */
        mViewPager.post(() -> {
            mViewPager.setAdapter(new TeamListFragmentPagerAdapter(getChildFragmentManager()));
            mTabs.setViewPager(mViewPager);
        });

        return v;
    }
}
