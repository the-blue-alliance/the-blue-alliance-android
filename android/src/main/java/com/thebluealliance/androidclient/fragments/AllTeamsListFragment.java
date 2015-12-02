package com.thebluealliance.androidclient.fragments;


import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.Utilities;
import com.thebluealliance.androidclient.binders.TeamTabBinder;
import com.thebluealliance.androidclient.subscribers.TeamTabSubscriber;
import com.thebluealliance.androidclient.views.SlidingTabs;

import rx.Observable;

public class AllTeamsListFragment extends DatafeedFragment<Integer, Integer, TeamTabSubscriber, TeamTabBinder> {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_team_list_fragment_pager, container, false);
        ViewPager viewPager = (ViewPager) v.findViewById(R.id.team_pager);
        // Make this ridiculously big
        viewPager.setOffscreenPageLimit(50);
        viewPager.setPageMargin(Utilities.getPixelsFromDp(getActivity(), 16));
        SlidingTabs tabs = (SlidingTabs) v.findViewById(R.id.team_pager_tabs);
        ViewCompat.setElevation(tabs, getResources().getDimension(R.dimen.toolbar_elevation));

        mBinder.viewPager = viewPager;
        mBinder.fragmentManager = getChildFragmentManager();
        mBinder.tabs = tabs;

        return v;
    }

    @Override
    protected void inject() {
        mComponent.inject(this);
    }

    @Override
    protected Observable<? extends Integer> getObservable(String tbaCacheHeader) {
        return mDatafeed.getCache().fetchLargestTeamNumber();
    }

    @Override
    protected String getRefreshTag() {
        return "all_teams";
    }
}
