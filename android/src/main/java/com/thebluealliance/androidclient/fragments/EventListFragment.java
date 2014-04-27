package com.thebluealliance.androidclient.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.background.PopulateEventList;

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
        return inflater.inflate(R.layout.fragment_events, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        new PopulateEventList(this, mYear, mWeek, mTeamKey).execute();
    }
}
