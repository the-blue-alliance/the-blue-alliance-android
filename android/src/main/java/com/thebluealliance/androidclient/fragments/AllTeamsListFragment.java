package com.thebluealliance.androidclient.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.astuetz.PagerSlidingTabStrip;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.adapters.TeamListAdapter;

public class AllTeamsListFragment extends Fragment {

    private int mYear;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragmet_team_list_fragment_pager, container, false);
        ViewPager pager = (ViewPager) v.findViewById(R.id.team_pager);
        pager.setAdapter(new TeamListAdapter(getChildFragmentManager()));

        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) v.findViewById(R.id.team_pager_tabs);
        tabs.setViewPager(pager);
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((ViewPager) getView().findViewById(R.id.team_pager)).getAdapter().notifyDataSetChanged();
    }
}
