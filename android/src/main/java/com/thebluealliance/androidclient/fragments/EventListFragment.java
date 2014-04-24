package com.thebluealliance.androidclient.fragments;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.background.PopulateEventList;
import com.thebluealliance.androidclient.interfaces.ActionBarSpinnerListener;

/**
 * File created by phil on 4/20/14.
 */
public class EventListFragment extends Fragment {

    public static final int EVENT_LIST_FOR_YEAR = 1;
    public static final int EVENT_LIST_FOR_TEAM_YEAR = 2;
    public static final int EVENT_LIST_FOR_YEAR_WEEK = 3;

    private int mListType;
    private int mYear;
    private int mWeek;
    private String mTeamKey;


    private View eventList;

    public EventListFragment(int listType, int year) {
        super();
        mListType = listType;
        mYear = year;
    }

    public EventListFragment(int listType, String teamKey, int year) {
        super();
        mListType = listType;
        mTeamKey = teamKey;
        mYear = year;
    }

    public EventListFragment(int listType, int year, int week) {
        mListType = listType;
        mYear = year;
        mWeek = week;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (eventList == null)
            eventList = inflater.inflate(R.layout.fragment_events, null);
        return eventList;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("EventListFragment", "onResume()");
        new PopulateEventList(this).execute("" + mYear, "week" + mWeek);
    }
}
