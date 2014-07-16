package com.thebluealliance.androidclient.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.astuetz.PagerSlidingTabStrip;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.Utilities;
import com.thebluealliance.androidclient.adapters.TeamListFragmentPagerAdapter;

public class AllTeamsListFragment extends Fragment {

    private ViewPager mViewPager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_team_list_fragment_pager, container, false);
        mViewPager = (ViewPager) v.findViewById(R.id.team_pager);
        mViewPager.setAdapter(new TeamListFragmentPagerAdapter(getChildFragmentManager()));
        mViewPager.setPageMargin(Utilities.getPixelsFromDp(getActivity(), 16));

        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) v.findViewById(R.id.team_pager_tabs);
        tabs.setViewPager(mViewPager);
        return v;
    }
}
