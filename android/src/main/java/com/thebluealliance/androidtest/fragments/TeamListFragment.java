package com.thebluealliance.androidtest.fragments;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidtest.background.PopulateTeamList;

/**
 * File created by phil on 4/20/14.
 */
public class TeamListFragment extends Fragment implements ActionBar.TabListener {

    private Activity activity;
    private ActionBar bar;
    private View teamList;

    public TeamListFragment(){
        super();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        bar = activity.getActionBar();
        if(bar != null){
            //configure action bar title
            bar.setDisplayShowTitleEnabled(true);
            bar.setTitle(activity.getString(R.string.app_name_short)+" - "+activity.getString(R.string.tab_teams));
            bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
            bar.setDisplayShowCustomEnabled(false);

            bar.removeAllTabs();
            bar.addTab(bar.newTab().setText("1-999").setTag(0).setTabListener(this));
            bar.addTab(bar.newTab().setText("1000's").setTag(1).setTabListener(this));
            bar.addTab(bar.newTab().setText("2000's").setTag(2).setTabListener(this));
            bar.addTab(bar.newTab().setText("3000's").setTag(3).setTabListener(this));
            bar.addTab(bar.newTab().setText("4000's").setTag(4).setTabListener(this));
            bar.addTab(bar.newTab().setText("5000's").setTag(5).setTabListener(this));
            //TODO again, select the proper tab based on the SavedInstanceState
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(teamList==null)
            teamList = inflater.inflate(R.layout.fragment_teams,null);
        if(activity == null)
            activity = getActivity();

        return teamList;
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        int start;
        if(bar == null || bar.getSelectedTab()==null)
            start = 0;
        else
            start = (Integer)bar.getSelectedTab().getTag();

        if(teamList==null)
            teamList = getActivity().getLayoutInflater().inflate(R.layout.fragment_teams,null);

        new PopulateTeamList(activity,teamList).execute(start);
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }
}
