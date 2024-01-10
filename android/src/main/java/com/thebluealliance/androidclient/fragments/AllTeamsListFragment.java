package com.thebluealliance.androidclient.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.view.ViewCompat;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.Utilities;
import com.thebluealliance.androidclient.binders.TeamTabBinder;
import com.thebluealliance.androidclient.databinding.FragmentTeamListFragmentPagerBinding;
import com.thebluealliance.androidclient.subscribers.TeamTabSubscriber;

import dagger.hilt.android.AndroidEntryPoint;
import rx.Observable;

@AndroidEntryPoint
public class AllTeamsListFragment extends DatafeedFragment<Integer, Integer, FragmentTeamListFragmentPagerBinding, TeamTabSubscriber, TeamTabBinder> {

    public static final String SELECTED_TAB = "selected_tab";

    private ViewPager2 mViewPager;
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
        mViewPager = v.findViewById(R.id.team_pager);
        int pageMargin = Utilities.getPixelsFromDp(requireActivity(), 16);
        mViewPager.setPageTransformer(new MarginPageTransformer(pageMargin));
        mBinder.setInitialTab(mInitialTab);

        TabLayout tabs = v.findViewById(R.id.team_pager_tabs);
        ViewCompat.setElevation(tabs, getResources().getDimension(R.dimen.toolbar_elevation));

        mBinder.viewPager = mViewPager;
        mBinder.parentFragment = this;
        mBinder.tabs = tabs;
        mBinder.setupAdapter();

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
    protected Observable<? extends Integer> getObservable(String tbaCacheHeader) {
        return mDatafeed.getCache().fetchLargestTeamNumber();
    }

    @Override
    protected String getRefreshTag() {
        return "all_teams";
    }
}
