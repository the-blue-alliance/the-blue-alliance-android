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

    public static final String SELECTED_TAB = "selected_tab";

    private ViewPager mViewPager;
    private int mInitialTab;

    public static AllTeamsListFragment newInstance(int tab) {
        AllTeamsListFragment f = new AllTeamsListFragment();
        Bundle args = new Bundle();
        args.putInt(SELECTED_TAB, tab);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mInitialTab = savedInstanceState.getInt(SELECTED_TAB, 0);
        } else if (getArguments() != null) {
            mInitialTab = getArguments().getInt(SELECTED_TAB, 0);
        } else {
            mInitialTab = 0;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_team_list_fragment_pager, container, false);
        mViewPager = (ViewPager) v.findViewById(R.id.team_pager);
        // Make this ridiculously big
        mViewPager.setOffscreenPageLimit(50);
        mViewPager.setPageMargin(Utilities.getPixelsFromDp(getActivity(), 16));
        mBinder.setInitialTab(mInitialTab);

        SlidingTabs tabs = (SlidingTabs) v.findViewById(R.id.team_pager_tabs);
        ViewCompat.setElevation(tabs, getResources().getDimension(R.dimen.toolbar_elevation));

        mBinder.viewPager = mViewPager;
        mBinder.fragmentManager = getChildFragmentManager();
        mBinder.tabs = tabs;

        return v;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SELECTED_TAB, mViewPager.getCurrentItem());
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_TAB)) {
            mViewPager.setCurrentItem(savedInstanceState.getInt(SELECTED_TAB));
        }
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
