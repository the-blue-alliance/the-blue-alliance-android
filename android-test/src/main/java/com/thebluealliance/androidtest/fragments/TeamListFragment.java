package com.thebluealliance.androidtest.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidtest.background.PopulateTeamList;

/**
 * File created by phil on 4/20/14.
 */
public class TeamListFragment extends Fragment {

    public TeamListFragment(){
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View teamList = inflater.inflate(R.layout.fragment_teams,null);
        new PopulateTeamList(getActivity(),teamList).execute("");
        return teamList;
    }
}
